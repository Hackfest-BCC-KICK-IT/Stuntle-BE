package bcc.stuntle.app.chat.service;

import bcc.stuntle.app.chat.repository.ChatMessageRepository;
import bcc.stuntle.app.chat.repository.ChatRepository;
import bcc.stuntle.app.chat.repository.ChatResponseRepository;
import bcc.stuntle.constant.ChatRedisConstant;
import bcc.stuntle.constant.OpenApiConstant;
import bcc.stuntle.constant.SecurityConstant;
import bcc.stuntle.entity.*;
import bcc.stuntle.util.ObjectMapperUtils;
import bcc.stuntle.util.ResponseUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@Slf4j
public class ChatService implements IChatService{

    /*
    chat usage repository
     */
    @Autowired
    private ChatRepository repository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ChatResponseRepository chatResponseRepository;

    @Autowired
    private ReactiveRedisTemplate<String, String> redisTemplate;

    @Value("${openai.key}")
    private String apiKey;

    @Override
    @SneakyThrows
    public Mono<ResponseEntity<Response<OpenApiCreateMessage>>> create(Long ortuId, String message) {
        OpenApiRequest req = OpenApiRequest
                .builder()
                .model(OpenApiConstant.MODEL)
                .messages(List.of(
                        new OpenApiRequest.Payload(OpenApiConstant.ROLE, message + OpenApiConstant.NOTES)
                ))
                .build();
        return WebClient.create(OpenApiConstant.URL)
                .post()
                .headers((h) -> {
                    h.setContentType(MediaType.APPLICATION_JSON);
                    h.add(HttpHeaders.AUTHORIZATION, String.format("%s %s", SecurityConstant.BEARER, this.apiKey));
                })
                .body(Mono.just(req), OpenApiResponse.class)
                .retrieve()
                .bodyToMono(OpenApiResponse.class)
                .flatMap((d) -> {
                    var tempResponse = d.getChoices().get(0).message().content();
                    ChatMessage chatMessage = ChatMessage
                            .builder()
                            .message(message)
                            .fkOrtuId(ortuId)
                            .createdAt(LocalDate.now())
                            .build();
                    ChatResponse chatResponse = ChatResponse
                            .builder()
                            .response(tempResponse)
                            .fkOrtuId(ortuId)
                            .createdAt(LocalDate.now())
                            .build();

                    var response = ObjectMapperUtils.readValue(tempResponse, OpenApiCreateMessage.class);

                    if(response.getKeywordType() != null){
                        if(response.getKeywordType().equals("keywordBayi")){
                            response.setMessage(
                                    response.getMessage() + "\n" + """
                                    Untuk memastikannya lebih lanjut, Anda dapat membaca panduannya pada menu artikel dengan mencari keyword "Tanda Bahaya Kehamilan" dan apabila ciri-ciri dari hal yang anda rasakan saat ini terdapat dalam artikel, segera meminta pertolongan melalui orang terdekat disekitar Anda, ataupun melalui fitur ajukan bantuan    
                                    """
                            );
                        } else if(response.getKeywordType().equals("keywordAnak")){
                            response.setMessage(
                                    response.getMessage() + "\n" + """
                                    Untuk memastikannya lebih lanjut, Anda dapat membaca panduannya pada menu artikel dengan mencari keyword "Gangguan Tumbuh Kembang Anak" dan apabila ciri-ciri dari anak Anda saat ini terdapat dalam artikel, segera meminta pertolongan melalui orang terdekat disekitar Anda, ataupun melalui fitur ajukan bantuan
                                    """
                            );
                        }
                    }

                    Mono<ChatMessage> chatMessageMono = this.chatMessageRepository.save(chatMessage);
                    Mono<ChatResponse> chatResponseMono = this.chatResponseRepository.save(chatResponse);
                    var ops = this.redisTemplate.opsForValue();
                    var removeKey = this.redisTemplate
                            .keys(ChatRedisConstant.ALL)
                            .flatMap((key) -> ops.delete(key).doOnNext((data) -> log.info("delete key {}", key)))
                            .collectList();
                    return Mono.zip(chatMessageMono, chatResponseMono, removeKey)
                            .map((v) -> this.repository.save(
                                    ChatResponseUsage
                                            .builder()
                                            .totalTokens(d.getUsage().totalTokens())
                                            .fkChatResponse(v.getT2().getId())
                                            .fkOrtuId(ortuId)
                                            .createdAt(LocalDate.now())
                                            .build())
                            )
                            .flatMap((v) -> v)
                            .then(Mono.fromCallable(() -> ResponseUtil.sendResponse(
                                    HttpStatus.OK,
                                    Response
                                            .<OpenApiCreateMessage>builder()
                                            .success(true)
                                            .message("sukses membuat pertanyaan ke open ai")
                                            .data(response)
                                            .build()
                            )));
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    @SneakyThrows
    public Mono<ResponseEntity<Response<OpenApiClientResponse>>> get(Long ortuId) {
        var ops = this.redisTemplate.opsForValue();
        var key = String.format(ChatRedisConstant.GET_ORTU_ID, ortuId);
        Mono<List<ChatMessage>> chatMessageFlux = this.chatMessageRepository.findByOrtuId(ortuId).collectList();
        Mono<List<ChatResponse>> chatResponseFlux = this.chatResponseRepository.findByOrtuId(ortuId).collectList();
        return ops.get(key)
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("redis result null on ChatService.get(ortuId)");
                    return Mono.zip(
                                    Mono.from(chatMessageFlux),
                                    Mono.from(chatResponseFlux)
                            )
                            .map((v) -> {
                                List<ChatMessage> listMessage = v.getT1();
                                List<ChatResponse> listResponse = v.getT2();
                                Map<LocalDate, List<ChatMessage>> mapMessage = new LinkedHashMap<>();
                                Map<LocalDate, List<ResponseMessage>> mapResponse = new LinkedHashMap<>();
                                listMessage.forEach((message) -> {
                                    var value = mapMessage.get(message.getCreatedAt());
                                    if(value == null){
                                        var mapListValue = new ArrayList<ChatMessage>();
                                        mapListValue.add(message);
                                        mapMessage.put(message.getCreatedAt(), mapListValue);
                                    } else {
                                        value.add(message);
                                    }
                                });
                                listResponse.forEach((response) -> {
                                    var value = mapResponse.get(response.getCreatedAt());
                                    var d = ObjectMapperUtils.readValue(response.getResponse(), OpenApiCreateMessage.class);
                                    var responseMessage = ResponseMessage
                                            .builder()
                                            .message(d.getMessage())
                                            .build();
                                    if(value == null){
                                        var mapListValue = new ArrayList<ResponseMessage>();
                                        mapListValue.add(responseMessage);
                                        mapResponse.put(response.getCreatedAt(), mapListValue);
                                    } else {
                                        value.add(responseMessage);
                                    }
                                });
                                return List.of(mapMessage, mapResponse);
                            })
                            .map(ObjectMapperUtils::writeValueAsString);
                }))
                .flatMap((listStr) -> {
                    var res = ObjectMapperUtils.readListValue(listStr, Map.class);
                    return ops.set(key, listStr, Duration.ofMinutes(1))
                            .then(Mono.just(res));
                })
                .map((v) -> {
                    var mapValue = v.get(0);
                    var mapResponse = v.get(1);
                    var listListChatMessage = mapValue.values().stream().toList();
                    var listListChatResponse = mapResponse.values().stream().toList();
                    return ResponseUtil.sendResponse(
                            HttpStatus.OK,
                            Response
                                    .<OpenApiClientResponse>builder()
                                    .message("sukses mendapatkan history chat")
                                    .success(true)
                                    .data(
                                            OpenApiClientResponse
                                                    .builder()
                                                    .messages(listListChatMessage)
                                                    .responses(listListChatResponse)
                                                    .build()
                                    )
                                    .build()
                    );
                })
                .subscribeOn(Schedulers.boundedElastic());
    }
}
