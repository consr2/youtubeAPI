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
						
						JSONParser parser = new JSONParser();
						JSONObject jsonObj = (JSONObject) parser.parse(nodeData);
						
						//json 해체작업
						JSONObject contents = (JSONObject) jsonObj.get("contents");
						JSONObject twoColumnBrowseResultsRenderer = (JSONObject) contents.get("twoColumnBrowseResultsRenderer");
						JSONArray tabs = (JSONArray) twoColumnBrowseResultsRenderer.get("tabs");
						JSONObject parserArray = (JSONObject) tabs.get(0);
						JSONObject tabRenderer = (JSONObject) parserArray.get("tabRenderer");
						JSONObject content = (JSONObject) tabRenderer.get("content");
						
						///모든 동영상 목록
						JSONObject sectionListRenderer = (JSONObject) content.get("sectionListRenderer");
						JSONArray contents2 = (JSONArray) sectionListRenderer.get("contents");
						JSONObject parserArray2 = (JSONObject) contents2.get(0);
						JSONObject itemSectionRenderer = (JSONObject) parserArray2.get("itemSectionRenderer");
						
						//대표 동영상 정보
						JSONArray contents3 = (JSONArray) itemSectionRenderer.get("contents");
						JSONObject parserArray3 = (JSONObject) contents3.get(0);
						JSONObject channelVideoPlayerRenderer = (JSONObject) parserArray3.get("channelVideoPlayerRenderer");
						
						//조회수
						JSONObject viewCountText = (JSONObject) channelVideoPlayerRenderer.get("viewCountText");
						//비디오 id
						String videoId1 = (String) channelVideoPlayerRenderer.get("videoId");
						//제목, 작성자, 등록경과일, 영상길이, 조회수
						JSONObject title = (JSONObject) channelVideoPlayerRenderer.get("title");
						JSONObject accessibility = (JSONObject) title.get("accessibility");
						JSONObject accessibilityData = (JSONObject) accessibility.get("accessibilityData");
						
						
						//비디오 ID
						Object videoId = JsonPath.read(nodeData, "$.contents.twoColumnBrowseResultsRenderer.tabs[*].tabRenderer.content.sectionListRenderer.contents[*]."
								+ "itemSectionRenderer.contents[*].channelVideoPlayerRenderer.videoId");
						//제목, 작성자, 등록경과일, 영상길이, 조회수
						Object titleInfo = JsonPath.read(nodeData, "$.contents.twoColumnBrowseResultsRenderer.tabs[*].tabRenderer.content.sectionListRenderer.contents[*]."
								+ "itemSectionRenderer.contents[*].channelVideoPlayerRenderer.title.accessibility.accessibilityData.label");
						
						
						System.out.println("현재 뽑은 내용 : " + videoId);
						System.out.println("현재 뽑은 내용 : " + titleInfo);
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
		return "<iframe width=\"560\" height=\"315\" src=\"https://youtube.com/shorts/dwyM4RfOBfg?feature=share\"></iframe>";
	}
}
