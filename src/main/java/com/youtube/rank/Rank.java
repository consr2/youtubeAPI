package com.youtube.rank;

import java.io.IOException;

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

@RestController
public class Rank {
	
	@GetMapping("/youtube/{id}")
	public String getYoutuber(@PathVariable String id) {
		Document doc = null;
		String user_agent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36";
		String url = "https://www.youtube.com/";
		
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
						
						//배열 해체작업
						JSONObject contents = (JSONObject) jsonObj.get("contents");
						JSONObject twoColumnBrowseResultsRenderer = (JSONObject) contents.get("twoColumnBrowseResultsRenderer");
						JSONArray tabs = (JSONArray) twoColumnBrowseResultsRenderer.get("tabs");
						JSONObject tabRenderer = (JSONObject) tabs.get(0);
						JSONObject content = (JSONObject) tabRenderer.get("content");
						//JSONObject richGridRenderer = (JSONObject) content.get("richGridRenderer");
						//JSONArray contents2 = (JSONArray) richGridRenderer.get("contents");
						//JSONObject richItemRenderer = (JSONObject) contents2.get(0);
						//JSONObject content2 = (JSONObject) richItemRenderer.get("content");
						
						
						
						
						System.out.println("현재 뽑은 내용 : " + content);
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
		return "<iframe width=\"560\" height=\"315\" src=\"https://www.youtube.com/embed/ikBAIDVomQ8\"></iframe>";
	}
}
