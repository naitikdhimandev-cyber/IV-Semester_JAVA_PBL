package com.smartexam;

import com.smartexam.ui.MainDashboard;
import com.smartexam.db.DBInitializer;

public class Main {
    public static void main(String[] args) {
        DBInitializer.initialize();
        new MainDashboard();
    }
}
