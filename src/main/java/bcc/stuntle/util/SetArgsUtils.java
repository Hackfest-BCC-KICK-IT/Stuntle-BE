package bcc.stuntle.util;

import io.lettuce.core.SetArgs;

import java.time.Duration;

public class SetArgsUtils {

    public static SetArgs buildMinute(int number){
        return SetArgs
                .Builder
                .ex(Duration.ofMinutes(number));
    }

    public static SetArgs buildSecond(int number){
        return SetArgs
                .Builder
                .ex(Duration.ofSeconds(number));
    }
}
