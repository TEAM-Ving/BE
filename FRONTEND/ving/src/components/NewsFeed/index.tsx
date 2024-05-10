'use client'

import React, { useEffect, useState } from 'react';
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';
import * as styles from './index.css';
import { line } from "@/styles/common.css";
import useAuthStore from '@/store/AuthStore';

interface Event {
  isDonation: boolean;
  username: string;
  nickname: string;
  choco: number;
}

export default function NewsFeed() {
  const { userData } = useAuthStore()
  const [events, setEvents] = useState<Event[]>([]);
  const [stompClient, setStompClient] = useState<Stomp.Client | null>(null);

  const roomId = btoa(userData.username);

  const onMessageReceived = (msg: string) => {
    const event: Event = JSON.parse(msg.body);
    setEvents(prevEvents => [...prevEvents, event]);
    console.log("Event received:", event);
  };

  const connect = () => {
    const socket = new SockJS('http://k10a203.p.ssafy.io/ws');
    const client = Stomp.over(socket);

    client.reconnect_delay = 5000;
    client.debug = (str) => {
      console.log('STOMP Debug:', str);
    };

    client.onConnect = () => {
      console.log("WebSocket 연결 완료");
      client.subscribe(`/sub/streamer/${roomId}`, onMessageReceived, {
        id: `sub-${roomId}`,
        ack: 'client'
      });
    };

    client.onDisconnect = () => {
      console.log("WebSocket 연결 해제 완료");
    };

    client.activate();
    setStompClient(client);
  };

  useEffect(() => {
    connect();
    return () => {
      stompClient?.deactivate();
      console.log("WebSocket 연결 해제 시도 중...");
    };
  }, []);

  return (
    <div className={styles.newsFeedContainer}>
      <div className={styles.title}>뉴스피드</div>
      <hr className={line} />
      <div className={styles.newsFeedContent}>
        {events.map((event, index) => (
          <div key={index} className={styles.newsFeedItem}>
            {event.isDonation ?
              `🍫 ${event.nickname} 님이 ${event.choco} 초코를 후원했습니다.` :
              `🎉 ${event.nickname} 님이 팔로우했습니다.`
            }
          </div>
        ))}
      </div>
    </div>
  );
}
