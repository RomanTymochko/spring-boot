package com.example.springboot;

import net.minidev.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import net.minidev.json.JSONObject;

import java.io.IOException;

@Controller
public class MainController {
	@Autowired
	private Statistics statistics;

	@GetMapping("/")
	public String getIndex(Model model){
		return "index";
	}

	@ResponseBody
	@PostMapping("/getStatisticInfo")
	public JSONArray getStats(Model model) throws IOException {
		return statistics.handWrittenNumbersRecognition();
	}
}
