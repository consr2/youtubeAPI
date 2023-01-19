package com.youtube.rank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Youtuber {
	
	private String username;
	
	private String totalView;

	private List<Map<String, String>> videoInfo = new ArrayList<>();

	public Youtuber(String username, List<String> video, String totalView) {
		
		this.username = username;
		this.totalView = totalView;
		
		for(int i = 0; i < 8; i++) {
			String text = video.get(i);
			if(i%2 == 0) {
				
			}else {
				String title = text.substring(0, text.indexOf(" 게시자: "));
				String elapsed = text.substring(text.indexOf(username,text.indexOf(" 게시자: "))+username.length()+1, text.indexOf(" 조회수 ")-7);
				String time = text.substring(text.indexOf(" 전 ",text.indexOf(" 게시자:")+5)+3, text.indexOf(" 조회수 "));
				String view = text.substring(text.indexOf("조회수 ",text.indexOf(" 게시자:"))+4, text.length());
				
				Map<String, String> instuntMap = new HashMap<>();
				
				
				instuntMap.put("비디오 ID", video.get(i-1));
				instuntMap.put("제목", title);
				instuntMap.put("영상길이", time);
				instuntMap.put("조회수", view);
				instuntMap.put("경과시간", elapsed);
				
				this.videoInfo.add(instuntMap);
				
			}
		}
	}

	public Youtuber() {}
	
	
	
	
	
}
