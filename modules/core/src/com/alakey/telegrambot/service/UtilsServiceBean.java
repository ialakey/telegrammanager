package com.alakey.telegrambot.service;

import org.springframework.stereotype.Service;

@Service(UtilsService.NAME)
public class UtilsServiceBean implements UtilsService {

    @Override
    public boolean isInteger(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            int i = Integer.parseInt(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}