import {WebPlugin} from '@capacitor/core';

import {VoiceRecorderImpl} from './VoiceRecorderImpl';
import type {CurrentRecordingStatus, GenericResponse, PeaksResponse, RecordingData, VoiceRecorderPlugin} from './definitions';


export class VoiceRecorderWeb extends WebPlugin implements VoiceRecorderPlugin {
    getPeaks(): Promise<PeaksResponse> {
     return new Promise<PeaksResponse>(resolve => {
        resolve({
            peakPower: 0,
            averagePower: 0
        })
     })
    }

    private voiceRecorderInstance = new VoiceRecorderImpl();

    public canDeviceVoiceRecord(): Promise<GenericResponse> {
        return VoiceRecorderImpl.canDeviceVoiceRecord();
    }

    public hasAudioRecordingPermission(): Promise<GenericResponse> {
        return VoiceRecorderImpl.hasAudioRecordingPermission();
    }

    public requestAudioRecordingPermission(): Promise<GenericResponse> {
        return VoiceRecorderImpl.requestAudioRecordingPermission();
    }

    public startRecording(): Promise<GenericResponse> {
        return this.voiceRecorderInstance.startRecording();
    }

    public stopRecording(): Promise<RecordingData> {
        return this.voiceRecorderInstance.stopRecording();
    }

    public pauseRecording(): Promise<GenericResponse> {
        return this.voiceRecorderInstance.pauseRecording();
    }

    public resumeRecording(): Promise<GenericResponse> {
        return this.voiceRecorderInstance.resumeRecording();
    }

    public getCurrentStatus(): Promise<CurrentRecordingStatus> {
        return this.voiceRecorderInstance.getCurrentStatus();
    }

}
