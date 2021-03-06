package knf.kuma.videoservers;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import org.jsoup.Jsoup;

import knf.kuma.commons.PatternUtil;

import static knf.kuma.videoservers.VideoServer.Names.FIRE;

/**
 * Created by Jordy on 11/01/2018.
 */

public class FireServer extends Server {
    public FireServer(Context context, String baseLink) {
        super(context, baseLink);
    }

    @Override
    public boolean isValid() {
        return baseLink.contains("efire.php");
    }

    @Override
    public String getName() {
        return FIRE;
    }

    @Nullable
    @Override
    VideoServer getVideoServer() {
        try {
            String frame = PatternUtil.extractLink(baseLink);
            String media_func = Jsoup.connect(frame).get().select("script").last().outerHtml();
            String download = Jsoup.connect(PatternUtil.extractLink(media_func)).get().select("a[href~=http://download.*]").first().attr("href");
            return new VideoServer(FIRE, new Option(null, download));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
