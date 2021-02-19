package com.example.springboot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import net.minidev.json.JSONObject;

import java.io.IOException;

@Controller
public class MainController {
	@Autowired
	Statistics statistics = new Statistics();

	@GetMapping("/")
	public String getIndex(Model model){
		return "index";
	}

	@ResponseBody
	@PostMapping("/getStatisticInfo")
	public JSONObject getStats(Model model) throws IOException {
		JSONObject json = new JSONObject();
		statistics.handWrittenNumbersRecognition();

		return json;
	}
}
