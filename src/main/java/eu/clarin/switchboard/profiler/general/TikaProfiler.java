package eu.clarin.switchboard.profiler.general;

import com.google.common.collect.ImmutableSet;
import eu.clarin.switchboard.profiler.api.Profile;
import eu.clarin.switchboard.profiler.api.Profiler;
import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.Detector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.parser.AutoDetectParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class TikaProfiler implements Profiler {
    private static final Logger LOGGER = LoggerFactory.getLogger(TikaProfiler.class);
    private static final Set<String> CERTAIN_MEDIATYPES = ImmutableSet.<String>builder()
            .add("application/pdf")
            .add("application/rtf")
            .add("application/msword")
            .add("application/vnd.ms-excel")
            .add("application/vnd.openxmlformats-officedocument.presentationml.presentation")
            .add("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            .add("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
            .add("application/zip")
            .add("application/x-gzip")
            .add("audio/vnd.wave")
            .add("audio/x-wav")
            .add("audio/wav")
            .add("audio/mp3")
            .add("audio/mp4")
            .add("audio/x-mpeg")
            .add("model/prs.ply")
            .build();

    Detector mediaTypeDetector;
    AutoDetectParser parser = new AutoDetectParser();

    public TikaProfiler(TikaConfig config) {
        mediaTypeDetector = config.getDetector();
    }

    @Override
    public List<Profile> profile(File file) throws IOException {
        String mediatype;
        try (TikaInputStream inputStream = TikaInputStream.get(file.toPath())) {
            Metadata metadata = new Metadata();
            metadata.add(TikaCoreProperties.RESOURCE_NAME_KEY, file.getName());
            mediatype = mediaTypeDetector.detect(inputStream, metadata).toString();
        }

        Profile.Builder profileBuilder = Profile.builder().mediaType(mediatype);
        if (CERTAIN_MEDIATYPES.contains(mediatype)) {
            profileBuilder.certain();
        }
        return Collections.singletonList(profileBuilder.build());
    }
}
