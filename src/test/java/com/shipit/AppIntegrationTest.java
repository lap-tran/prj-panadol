package com.shipit;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder;
import org.eclipse.jetty.io.RuntimeIOException;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.File;

public class AppIntegrationTest {
    @Test
    public void browseSite() throws InterruptedException {
        Thread recorder = (new Thread() {
            @Override
            public synchronized void run() {
                recordScreen("");
            }
        });
        recorder.start();

        // Create a new instance of the Firefox driver
        WebDriver driver = new FirefoxDriver();

        //Launch the Online Store Website
        driver.get("http://www.store.demoqa.com");

        driver.quit();

        // wait and quit
        synchronized (recorder) {
            // Print a Log In message to the screen
            System.out.println("Successfully opened the website www.Store.Demoqa.com");
        }
    }

    private static void recordScreen(String os) {
        String osDisplay, osFormat;
        if ("macos".equals(os)) {
            osDisplay = "1:";
            osFormat = "avfoundation";
        } else {
            osDisplay = ":0.0+0,0";
            osFormat = "x11grab";
        }
        try {
            int x = 0, y = 0, w = 640, h = 480; // specify the region of screen to grab

            File artifactDir = new File("target");
            System.out.println(artifactDir.getAbsolutePath());

            FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(artifactDir.getAbsolutePath() + "/recorder.mp4", w, h);
            recorder.setVideoQuality(2);
            recorder.setVideoCodec(13);
            recorder.setFormat("mp4");
            recorder.setFrameRate(12);
            recorder.setSampleRate(12);

            recorder.start();

            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(osDisplay);
            grabber.setFormat(osFormat);
            grabber.setImageWidth(w);
            grabber.setImageHeight(h);
            grabber.setFrameRate(12);
            grabber.start();

            for (int i = 0; i < 100; i++) {
                recorder.record(grabber.grabFrame(false, true, true, false));
            }

            grabber.stop();
            recorder.stop();
        } catch (FrameRecorder.Exception e) {
            throw new RuntimeIOException(e);
        } catch (FrameGrabber.Exception e) {
            throw new RuntimeIOException(e);
        }
    }
}
