package com.tchvu3.capacitorvoicerecorder;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;

import java.io.File;
import java.io.IOException;

public class CustomMediaRecorder {

    private final Context context;
    private MediaRecorder mediaRecorder;
    private File outputFile;
    private CurrentRecordingStatus currentRecordingStatus = CurrentRecordingStatus.NONE;
    private Handler handler;
    private Double peakLevel = 0.0;

    public CustomMediaRecorder(Context context) throws IOException {
        this.context = context;
        generateMediaRecorder();
    }

    private void generateMediaRecorder() throws IOException {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setAudioEncodingBitRate(96000);
        mediaRecorder.setAudioSamplingRate(44100);
        setRecorderOutputFile();
        mediaRecorder.prepare();
    }

    private void setRecorderOutputFile() throws IOException {
        File outputDir = context.getCacheDir();
        outputFile = File.createTempFile("voice_record_temp", ".aac", outputDir);
        outputFile.deleteOnExit();
        mediaRecorder.setOutputFile(outputFile.getAbsolutePath());
    }

    public void startRecording(boolean withMetrics) {
        mediaRecorder.start();
        currentRecordingStatus = CurrentRecordingStatus.RECORDING;
        if (withMetrics) {
            handler = new Handler();
            handler.post(updateMeter);
        }
    }

    public void stopRecording() {
        mediaRecorder.stop();
        mediaRecorder.release();
        currentRecordingStatus = CurrentRecordingStatus.NONE;
    }

    public File getOutputFile() {
        return outputFile;
    }

    public boolean pauseRecording() throws NotSupportedOsVersion {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            throw new NotSupportedOsVersion();
        }

        if (currentRecordingStatus == CurrentRecordingStatus.RECORDING) {
            mediaRecorder.pause();
            currentRecordingStatus = CurrentRecordingStatus.PAUSED;
            return true;
        } else {
            return false;
        }
    }

    public boolean resumeRecording() throws NotSupportedOsVersion {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            throw new NotSupportedOsVersion();
        }

        if (currentRecordingStatus == CurrentRecordingStatus.PAUSED) {
            mediaRecorder.resume();
            currentRecordingStatus = CurrentRecordingStatus.RECORDING;
            return true;
        } else {
            return false;
        }
    }

    public CurrentRecordingStatus getCurrentStatus() {
        return currentRecordingStatus;
    }

    public boolean deleteOutputFile() {
        return outputFile.delete();
    }

    public static boolean canPhoneCreateMediaRecorder(Context context) {
        return true;
    }

    public double getPeakLevel() {
        return peakLevel;
    }


    private Runnable updateMeter = new Runnable() {
        @Override
        public void run() {
            if (currentRecordingStatus == CurrentRecordingStatus.RECORDING) {
                int amplitude = mediaRecorder.getMaxAmplitude();
                double dBFS = 20 * Math.log10(amplitude / 32767.0);
                // Use the dBFS value as needed
                System.out.println("Peak dBFS: " + dBFS);
                peakLevel = dBFS;
                handler.postDelayed(this, 100); // Update every 100 milliseconds
            }
        }
    };

}
