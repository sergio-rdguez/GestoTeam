package com.gestoteam.controller;

import com.gestoteam.model.UserSettings;
import com.gestoteam.service.UserSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user-settings")
public class UserSettingsController {
    @Autowired
    private UserSettingsService userSettingsService;

    @GetMapping
    public UserSettings getSettings() {
        return userSettingsService.getSettings();
    }

    @PutMapping
    public UserSettings updateSettings(@RequestBody UserSettings updatedSettings) {
        return userSettingsService.updateSettings(updatedSettings);
    }
}
