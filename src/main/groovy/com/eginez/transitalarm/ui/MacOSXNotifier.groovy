package com.eginez.transitalarm.ui

class MacOSXNotifier implements Notifier {
    @Override
    void notify(String title, String message) {
        ["osascript", "-e", "display notification \"${message}\" with title \"${title}\""].execute()
    }
}
