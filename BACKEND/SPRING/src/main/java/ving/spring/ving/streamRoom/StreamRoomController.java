package ving.spring.ving.streamRoom;

import com.sun.tools.jconsole.JConsoleContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ving.spring.ving.global.dto.DateTimeFormmer;
import ving.spring.ving.s3.S3Service;
import ving.spring.ving.socket.Message;
import ving.spring.ving.socket.chat.ChatModel;
import ving.spring.ving.socket.chat.ChatModelService;
import ving.spring.ving.socket.room.RoomModel;
import ving.spring.ving.socket.room.RoomModelService;
import ving.spring.ving.streamRoom.alarm.AlarmEnum;
import ving.spring.ving.streamRoom.alarm.AlarmModel;
import ving.spring.ving.streamRoom.alarm.AlarmService;
import ving.spring.ving.subscription.SubscriptionModel;
import ving.spring.ving.subscription.SubscriptionService;
import ving.spring.ving.user.UserModel;
import ving.spring.ving.user.UserService;
import ving.spring.ving.video.VideoDto;
import ving.spring.ving.video.VideoModel;
import ving.spring.ving.video.VideoService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/stream")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class StreamRoomController {
    private final ChatModelService chatModelService;
    private final UserService userService;
    private final StreamRoomService streamRoomService;
    private final S3Service s3Service;
    private final SubscriptionService subscriptionService;
    private final AlarmService alarmService;
    private final DateTimeFormmer dateTimeFormmer;
    private final RoomModelService roomModelService;
    private final VideoService videoService;
    @GetMapping("/chatting")
    List<Message.RecordedChat> getChatting(@RequestParam("videoName") String videoName,@RequestParam("timeStamp") String timeStamp)
    {
        log.info(timeStamp);
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime spentTime = LocalDateTime.parse(timeStamp, formatter);
        Duration duration = Duration.between(spentTime, localDateTime);
        log.info(String.valueOf(duration.getSeconds()));
        return chatModelService.findChatModelsByTimeStamp(duration.getSeconds(), videoName);
    }

    @PatchMapping("/end")
    ResponseEntity<?> endStreaming()
    {
        try {
            UserModel streamer = userService.findCurrentUser();
            StreamRoomModel streamRoomModel = streamRoomService.findStreamRoomModelByStreamerAndIsEnd(streamer);

            Duration duration = Duration.between(LocalDateTime.now(), streamRoomModel.getCreatedAt());
            Integer seconds =  Long.valueOf(duration.toSeconds()).intValue();
            VideoModel videoModel = VideoModel.builder()
                    .videoName(streamRoomModel.getRoomName())
                    .videoSerial(streamRoomModel.getRoomId())
                    .videoplay(0)
                    .videoIsPublic(1)
                    .user(streamer)
                    .thumbnail(streamRoomModel.getRoomThumbnail())
                    .videoLength(seconds)
                    .videoLength(0)
                    .build();
            videoService.create(videoModel);
            log.info("비디오 저장 성공");
            streamRoomModel.setIsEnd(true);
            try
            {
                RoomModel roomModel = roomModelService.findByStreamer(streamer.getUserUsername());
                roomModelService.delete(roomModel);
            }
            catch (Exception e)
            {
                log.info("레전드 상황 발생");
            }

            streamRoomService.save(streamRoomModel);
            return ResponseEntity.ok().body(VideoDto.VideoEntity.builder()
                            .videoLength(videoModel.getVideoLength())
                            .videoSerial(videoModel.getVideoSerial())
                            .videoId(videoModel.getVideoId())
                            .title(videoModel.getVideoName())
                            .videoPlay(videoModel.getVideoplay())
                            .thumbnail(videoModel.getThumbnail())
                            .createdAt(dateTimeFormmer.transform(videoModel.getCreatedAt()))
                    .build()
            );
        } catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(value = "/tmpCreate", consumes = "multipart/form-data")
    ResponseEntity<?> tmpCreate(@ModelAttribute StreamRoomDto.CreateRoomRequest createRoomRequest)
    {
        try {
            UserModel streamer = userService.findCurrentUser();
            try
            {
                StreamRoomModel existingStreamRoomModel = streamRoomService.findStreamRoomModelByStreamerAndIsEnd(streamer);
                log.info("이미 방이 존재함");
                try
                {
                    RoomModel roomModel = roomModelService.findByStreamer(streamer.getUserUsername());
                    roomModelService.delete(roomModel);
                }
                catch (Exception e)
                {
                    log.info("레전드 상황 발생");
                }

                existingStreamRoomModel.setIsEnd(true);
                streamRoomService.save(existingStreamRoomModel);
            } catch (Exception e)
            {   
                log.info("방송 있음");
            }

            StreamRoomModel streamRoomModel = StreamRoomModel.builder()
                    .streamer(streamer)
                    .roomThumbnail(s3Service.saveMedia(createRoomRequest.getThumbNail(), createRoomRequest.getRoomName()))
                    .roomName(createRoomRequest.getRoomName())
                    .roomAgeLimit(createRoomRequest.getIsAdult())
                    .isEnd(false)
                    .build();
            streamRoomService.save(streamRoomModel);

            RoomModel roomModel = RoomModel.builder()
                    .createdAt(dateTimeFormmer.transform(LocalDateTime.now()))
                    .streamer(streamer.getUserUsername())
                    .title(streamRoomModel.getRoomName())
                    .viewers(0)
                    .build();
            log.info("몽고방 저장하겠습니다");
            roomModelService.save(roomModel);
            // 메세징큐로 영상 녹화하는 내용 추가해야함

            // 모든 구독 찾아서 더해줘야함
            List<SubscriptionModel> subscriptionModels =  subscriptionService.findSubscriptionModelsByStreamerAndNotification(streamer);
            for (SubscriptionModel subscriptionModel : subscriptionModels)
            {
                AlarmModel alarmModel = AlarmModel.builder()
                        .subscriptionModel(subscriptionModel)
                        .noticeContent(AlarmEnum.streaming)
                        .build();
                alarmService.save(alarmModel);
            }

            return ResponseEntity.ok().body(StreamRoomDto.CreateResponse.builder()
                            .streamKey(streamer.getUserUsername() + "_" + streamRoomModel.getRoomId().toString())
                            .build()
            );
        }
        catch (Exception e)
        {
            return  ResponseEntity.badRequest().body("안됨");
        }
    }


    @GetMapping("/getOnAir")
    ResponseEntity<?> getOnAir(@RequestParam String username)
    {
        try
        {
            UserModel userModel = userService.findByUserUsername(username).orElseThrow();
            StreamRoomModel streamRoomModel = streamRoomService.findStreamRoomModelByStreamerAndIsEnd(userModel);
            VideoDto.VideoEntity videoEntity = VideoDto.VideoEntity.builder()
                    .isFixed(false)
                    .videoPlay(0)
                    .videoId(-1)
                    .title(streamRoomModel.getRoomName())
                    .thumbnail(streamRoomModel.getRoomThumbnail())
                    .createdAt(dateTimeFormmer.transform(streamRoomModel.getCreatedAt()))
                    .build();
            return ResponseEntity.ok(videoEntity);

        } catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/findAll")
    ResponseEntity<?> findAll()
    {
        List<StreamRoomDto.StreamRoom> streamRooms = new ArrayList<>();

        for (StreamRoomModel streamRoomModel : streamRoomService.findAll())
        {
            if (roomModelService.existsByStreamer(streamRoomModel.getStreamer().getUserUsername()))
            {
                RoomModel roomModel = roomModelService.findByStreamer(streamRoomModel.getStreamer().getUserUsername());
                streamRooms.add(
                        StreamRoomDto.StreamRoom.builder()
                                .title(streamRoomModel.getRoomName())
                                .username(streamRoomModel.getStreamer().getUserUsername())
                                .viewers(roomModel.getViewers())
                                .roomId(streamRoomModel.getRoomId())
                                .nickname(streamRoomModel.getStreamer().getUserNickname())
                                .thumbnail(streamRoomModel.getRoomThumbnail())
                                .streamerThumbnail(streamRoomModel.getStreamer().getUserPhoto())
                                .createdAt(roomModel.getCreatedAt())
                                .build()
                );
            }
            else
            {
                streamRooms.add
                (
                        StreamRoomDto.StreamRoom.builder()
                                .title(streamRoomModel.getRoomName())
                                .username(streamRoomModel.getStreamer().getUserUsername())
                                .viewers(-1)
                                .roomId(streamRoomModel.getRoomId())
                                .thumbnail(streamRoomModel.getRoomThumbnail())
                                .streamerThumbnail(streamRoomModel.getStreamer().getUserPhoto())
                                .createdAt(dateTimeFormmer.transform(LocalDateTime.now()))
                                .build()
                );
            }

        }
        StreamRoomDto.FindAllResponse findAllResponse = StreamRoomDto.FindAllResponse.builder()
                .streamRooms(streamRooms)
                .build();
        return ResponseEntity.ok(findAllResponse);
    }
    @GetMapping("/getAlarm")
    ResponseEntity<?> getAlarm()
    {
        try {
            List<SubscriptionModel> subscriptionModels = subscriptionService.findSubscriptionModelsByFollowerAndNotification(userService.findCurrentUser());
            List<StreamRoomDto.Alarm> alarms = new ArrayList<>();

            for (SubscriptionModel subscriptionModel : subscriptionModels)
            {
                List<AlarmModel> alarmModels = alarmService.findAlarmModelsBySubscriptionModel(subscriptionModel);
                // alarmModels.addAll(alarmModels1);
                for (AlarmModel alarmModel : alarmModels)
                {
                    alarms.add(
                            StreamRoomDto.Alarm.builder()
                                    .streamer(subscriptionModel.getStreamer().getUserNickname())
                                    .type(alarmModel.getNoticeContent().toString())
                                    .build()
                    );
                }
            }
            return ResponseEntity.ok(StreamRoomDto.AlarmsDto.builder()
                    .alarms(alarms)
                    .build()
            );
        } catch (Exception e)
        {
            return ResponseEntity.badRequest().body(HttpStatus.BAD_REQUEST);
        }

    }
}
