package org.thoughtcrime.securesms.stickers;

import android.support.annotation.NonNull;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.data.DataFetcher;

import org.thoughtcrime.securesms.logging.Log;
import org.thoughtcrime.securesms.util.Hex;
import org.whispersystems.libsignal.InvalidMessageException;
import org.whispersystems.signalservice.api.SignalServiceMessageReceiver;

import java.io.IOException;
import java.io.InputStream;

/**
 * Downloads a sticker remotely. Used with Glide.
 */
public final class StickerRemoteUriFetcher implements DataFetcher<InputStream> {

  private static final String TAG = Log.tag(StickerRemoteUriFetcher.class);

  private final SignalServiceMessageReceiver receiver;
  private final StickerRemoteUri stickerUri;

  public StickerRemoteUriFetcher(@NonNull SignalServiceMessageReceiver receiver, @NonNull StickerRemoteUri stickerUri) {
    this.receiver   = receiver;
    this.stickerUri = stickerUri;
  }

  @Override
  public void loadData(@NonNull Priority priority, @NonNull DataCallback<? super InputStream> callback) {
    try {
      byte[]      packIdBytes  = Hex.fromStringCondensed(stickerUri.getPackId());
      byte[]      packKeyBytes = Hex.fromStringCondensed(stickerUri.getPackKey());
      InputStream stream       = receiver.retrieveSticker(packIdBytes, packKeyBytes, stickerUri.getStickerId());

      callback.onDataReady(stream);
    } catch (IOException | InvalidMessageException e) {
      callback.onLoadFailed(e);
    }
  }

  @Override
  public void cleanup() {

  }

  @Override
  public void cancel() {
    Log.d(TAG, "Canceled.");
  }

  @Override
  public @NonNull Class<InputStream> getDataClass() {
    return InputStream.class;
  }

  @Override
  public @NonNull DataSource getDataSource() {
    return DataSource.REMOTE;
  }
}
