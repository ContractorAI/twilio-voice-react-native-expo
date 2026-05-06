package com.twiliovoicereactnative;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;

import java.util.HashMap;
import java.util.Map;

class MediaPlayerManager {
  public enum SoundTable {
    INCOMING,
    OUTGOING,
    DISCONNECT,
    RINGTONE
  }
  private final SoundPool soundPool;
  private final Map<SoundTable, Integer> soundMap;
  private int activeStream;

  MediaPlayerManager(Context context) {
    soundPool = (new SoundPool.Builder())
      .setMaxStreams(2)
      .setAudioAttributes(
        new AudioAttributes.Builder()
          .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
          .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
          .build())
      .build();
    activeStream = 0;
    soundMap = new HashMap<>();
    soundMap.put(SoundTable.INCOMING, soundPool.load(context, R.raw.incoming, 1));
    soundMap.put(SoundTable.OUTGOING, soundPool.load(context, R.raw.outgoing, 1));
    soundMap.put(SoundTable.DISCONNECT, soundPool.load(context, R.raw.disconnect, 1));
    soundMap.put(SoundTable.RINGTONE, soundPool.load(context, R.raw.ringtone, 1));
  }

  public synchronized void play(final SoundTable sound) {
    if (activeStream != 0) {
      soundPool.stop(activeStream);
      activeStream = 0;
    }
    activeStream = soundPool.play(
      soundMap.get(sound),
      1.f,
      1.f,
      1,
      (SoundTable.DISCONNECT== sound) ? 0 : -1,
      1.f);
  }

  public synchronized void stop() {
    soundPool.stop(activeStream);
    activeStream = 0;
  }

  @Override
  protected void finalize() throws Throwable {
    soundPool.release();
    super.finalize();
  }
}
