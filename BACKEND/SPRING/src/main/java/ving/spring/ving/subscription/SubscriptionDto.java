package ving.spring.ving.subscription;

import lombok.*;

@Builder
@Getter
@Setter
public class SubscriptionDto {

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubscriptRequest
    {
        private String username;
    }

    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SubscriptResponse
    {
        private String username;
    }


    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DonationRequest
    {
        private String streamer;
        private String nickname;
        private Integer choco;
        private Boolean isTts;
        private String message;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChatDetailResponse
    {
        private String username;
        private String nickname;
        private String thumbnail;
        private String introduction;
        private String timeStamp;
    }
}
