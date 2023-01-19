package com.youtube.rank;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;

@RestController
public class Rank {
	
	@RequestMapping("/search")
	public Youtuber getYoutuberInfo(@RequestParam("id") String id) {
		
		//@유튜버 아이디 리턴 해줌
		String userId = searchId(id);
		
		Document doc = null;
		String user_agent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36";
		String url = "https://www.youtube.com/" + userId;
		
		Youtuber youtuber = new Youtuber();
		
		try {
			Connection.Response nvDocument = Jsoup.connect(url).userAgent(user_agent)
					.method(Connection.Method.GET)
					.execute();
			doc = nvDocument.parse();		
					
			Elements datas = doc.select("script");
			
			
			
			for(Element data : datas) {
				for(DataNode node : data.dataNodes()) {
					if(node.getWholeData().contains("var ytInitialData = ")) {
						
						String nodeData = node.getWholeData();
					
						nodeData = nodeData.replace("var ytInitialData = ", "");
						nodeData = nodeData.replace(nodeData.substring(nodeData.length() -1), "");
						
						
						//최근 동영상 ID
						List<String> recentVideoId = JsonPath.read(nodeData, "$.contents.twoColumnBrowseResultsRenderer.tabs[*].tabRenderer.content.sectionListRenderer.contents[*]."
								+ "itemSectionRenderer.contents[*].shelfRenderer.content..horizontalListRenderer.items[*].gridVideoRenderer.videoId");
						//최근 동영상 제목, 작성자, 등록경과일, 영상길이, 조회수
						List<String> recentVideoInfo = JsonPath.read(nodeData, "$.contents.twoColumnBrowseResultsRenderer.tabs[*].tabRenderer.content.sectionListRenderer.contents[*]."
								+ "itemSectionRenderer.contents[*].shelfRenderer.content..horizontalListRenderer.items[*].gridVideoRenderer.title.accessibility.accessibilityData.label");
						
						
						//총 조회수 가져오기
						String totalView = getView(userId);
						
						List<String> videoInfo = new ArrayList<>();
						for(int i = 0; i < 4; i++) {
							videoInfo.add(recentVideoId.get(i));
							videoInfo.add(recentVideoInfo.get(i));
						}
						
						youtuber = new Youtuber(id, videoInfo, totalView);
						
					}
				}
			}
			
			return youtuber;
		}catch(Exception e) {
			e.printStackTrace();
			return youtuber;
		}
	}
	
	@RequestMapping("/")
	public String test() {
		return "<iframe width=\"560\" height=\"315\" src=\"https://www.youtube.com/embed/HbKnQxnmPUQ\"></iframe>";
	}
	
	
	//한글로 검색한 유튜버 아이디 @ID로 변환 메서드
	public String searchId(String id) {
		
		//받은 파라미터를 url형식(ascll) 형식으로 변경한다
		try {
			id = URLEncoder.encode(id, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		String url = "https://www.youtube.com/results?search_query=" + id;
		String userId = null;
		
		//해당 유튜버의 아이디를 찾아 리턴한다
		try {
			Document doc = Jsoup.connect(url).get();
			Elements datas = doc.select("script");
			
			for(Element data : datas) {
				for(DataNode node : data.dataNodes()) {
					if(node.getWholeData().contains("var ytInitialData = ")) {
						String nodeData = node.getWholeData();
						
						nodeData = nodeData.replace("var ytInitialData = ", "");
						nodeData = nodeData.replace(nodeData.substring(nodeData.length() -1), "");
						
						
						Object getId = JsonPath.read(nodeData, "$.contents.twoColumnSearchResultsRenderer.primaryContents."
								+ "sectionListRenderer.contents[*].itemSectionRenderer.contents[*].channelRenderer.navigationEndpoint.commandMetadata.webCommandMetadata.url");
						
						userId = getId.toString();
						userId = userId.substring(4, userId.length()-2);
					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return userId;
	}
	
	
	
	public String getView(String userId) {
		String url = "https://www.youtube.com/" + userId + "/about";
		
		try {
			Document doc = Jsoup.connect(url).get();
			Elements datas = doc.select("script");
			Object getView = null;
			
			for(Element data : datas) {
				for(DataNode node : data.dataNodes()) {
					if(node.getWholeData().contains("var ytInitialData = ")) {
						String nodeData = node.getWholeData();
						
						nodeData = nodeData.replace("var ytInitialData = ", "");
						nodeData = nodeData.replace(nodeData.substring(nodeData.length() -1), "");
						
						getView = JsonPath.read(nodeData, "$.contents.twoColumnBrowseResultsRenderer.tabs[*]."
								+ "tabRenderer.content.sectionListRenderer.contents[*].itemSectionRenderer.contents[*].channelAboutFullMetadataRenderer.viewCountText.simpleText");
						
					}
				}
			}
			return getView.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return "0";
		}
	}
	
	
	
}
