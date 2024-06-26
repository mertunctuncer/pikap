package me.koply.pikap.api.event;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class NextTrackEvent extends AudioEvent {

    public final AudioTrack pastTrack;
    public final AudioTrack nextTrack;
    public final Reason reason;

    public NextTrackEvent(AudioTrack pastTrack, AudioTrack nextTrack, Reason reason) {
        this.pastTrack = pastTrack;
        this.nextTrack = nextTrack;
        this.reason = reason;
    }

    @Override
    public boolean isCancellable() {
        return false;
    }

    @Override
    public boolean isCanceled() {
        return false;
    }

    public enum Reason {
        TRACK_END, NEXT
    }
}