package com.luobo.scriptrunner.demo;

import com.luobo.scriptrunner.entity.Script;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {
    @Autowired
    ScriptRunner scriptRunner;

    @PostMapping("/test")
    @ResponseBody
    public String runScript(@RequestBody Script script) {
        return scriptRunner.sendCommand(script.getScriptContent());
    }
}
