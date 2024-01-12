package bcc.stuntle.configuration;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfiguration {

    @Value("${cloudinary.url}")
    private String cloudinaryUrl;

    @Bean
    public Cloudinary cloudinary(){
        Cloudinary cloudinary = new Cloudinary(cloudinaryUrl);
        cloudinary.config.secure = true;
        return cloudinary;
    }
}
