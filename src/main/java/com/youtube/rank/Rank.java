package com.youtube.rank;

import java.io.IOException;
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
import org.springframework.web.bind.annotation.RestController;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;

@RestController
public class Rank {
	
	@GetMapping("/youtube/{id}")
	public String getYoutuber(@PathVariable String id) {
		Document doc = null;
		String user_agent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36";
		String url = "https://www.youtube.com/@huehueman";
		
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
						
						//비디오 ID
						Object videoId = JsonPath.read(nodeData, "$.contents.twoColumnBrowseResultsRenderer.tabs[*].tabRenderer.content.sectionListRenderer.contents[*]."
								+ "itemSectionRenderer.contents[*].channelVideoPlayerRenderer.videoId");
						//제목, 작성자, 등록경과일, 영상길이, 조회수
						Object titleInfo = JsonPath.read(nodeData, "$.contents.twoColumnBrowseResultsRenderer.tabs[*].tabRenderer.content.sectionListRenderer.contents[*]."
								+ "itemSectionRenderer.contents[*].channelVideoPlayerRenderer.title.accessibility.accessibilityData.label");
						
						//최근 동영상 4개 ID
						List<String> recentVideoId = JsonPath.read(nodeData, "$.contents.twoColumnBrowseResultsRenderer.tabs[*].tabRenderer.content.sectionListRenderer.contents[*]."
								+ "itemSectionRenderer.contents[*].shelfRenderer.content..horizontalListRenderer.items[*].gridVideoRenderer.videoId");
						//최근 동영상 4개 제목, 작성자, 등록경과일, 영상길이, 조회수
						List<String> recentVideoInfo = JsonPath.read(nodeData, "$.contents.twoColumnBrowseResultsRenderer.tabs[*].tabRenderer.content.sectionListRenderer.contents[*]."
								+ "itemSectionRenderer.contents[*].shelfRenderer.content..horizontalListRenderer.items[*].gridVideoRenderer.title.accessibility.accessibilityData.label");
						
						
						System.out.println("현재 뽑은 내용 : " + videoId);
						System.out.println("현재 뽑은 내용 : " + titleInfo);
						System.out.println("현재 뽑은 내용 : " + recentVideoId);
						System.out.println("현재 뽑은 내용 : " + recentVideoInfo);
					}
				}
			}
			
			return "1";
		}catch(Exception e) {
			e.printStackTrace();
			return "0";
		}
	}
	
	@RequestMapping("/")
	public String test() {
		return "<iframe width=\"560\" height=\"315\" src=\"https://www.youtube.com/embed/HbKnQxnmPUQ\"></iframe>";
	}
}
