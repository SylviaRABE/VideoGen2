package transformations;

import configs.VideoGenConfigs;
import helpers.FFMPEGHelper;
import java.io.File;
import java.util.List;
import org.eclipse.emf.common.util.EList;
import org.xtext.example.mydsl.videoGen.AlternativesMedia;
import org.xtext.example.mydsl.videoGen.MandatoryMedia;
import org.xtext.example.mydsl.videoGen.Media;
import org.xtext.example.mydsl.videoGen.MediaDescription;
import org.xtext.example.mydsl.videoGen.OptionalMedia;
import org.xtext.example.mydsl.videoGen.VideoDescription;
import org.xtext.example.mydsl.videoGen.VideoGeneratorModel;
import utils.CommonUtils;
import utils.VideoGenUtils;

@SuppressWarnings("all")
public class VideoGenAnalysisTransformation {
  public static int getMaxDuration(final VideoGeneratorModel videoGen) {
    int _xblockexpression = (int) 0;
    {
      int maxDuration = 0;
      EList<Media> _medias = videoGen.getMedias();
      for (final Media media : _medias) {
        {
          if ((media instanceof AlternativesMedia)) {
            AlternativesMedia alternativeMedia = ((AlternativesMedia) media);
            int maxAlternativeDuration = 0;
            EList<MediaDescription> _medias_1 = alternativeMedia.getMedias();
            for (final MediaDescription alternative : _medias_1) {
              if ((alternative instanceof VideoDescription)) {
                VideoDescription videoDescription = ((VideoDescription) alternative);
                int alternativeDuration = FFMPEGHelper.getVideoDuration(videoDescription.getLocation());
                if ((maxAlternativeDuration < alternativeDuration)) {
                  maxAlternativeDuration = alternativeDuration;
                }
              }
            }
            int _maxDuration = maxDuration;
            maxDuration = (_maxDuration + maxAlternativeDuration);
          }
          if ((media instanceof MandatoryMedia)) {
            MediaDescription _description = ((MandatoryMedia)media).getDescription();
            if ((_description instanceof VideoDescription)) {
              MediaDescription _description_1 = ((MandatoryMedia)media).getDescription();
              VideoDescription videoDescription_1 = ((VideoDescription) _description_1);
              int _maxDuration_1 = maxDuration;
              int _videoDuration = FFMPEGHelper.getVideoDuration(videoDescription_1.getLocation());
              maxDuration = (_maxDuration_1 + _videoDuration);
            }
          }
          if ((media instanceof OptionalMedia)) {
            MediaDescription _description_2 = ((OptionalMedia)media).getDescription();
            if ((_description_2 instanceof VideoDescription)) {
              MediaDescription _description_3 = ((OptionalMedia)media).getDescription();
              VideoDescription videoDescription_2 = ((VideoDescription) _description_3);
              int _maxDuration_2 = maxDuration;
              int _videoDuration_1 = FFMPEGHelper.getVideoDuration(videoDescription_2.getLocation());
              maxDuration = (_maxDuration_2 + _videoDuration_1);
            }
          }
        }
      }
      _xblockexpression = maxDuration;
    }
    return _xblockexpression;
  }
  
  public static double getMinimalSize(final VideoGeneratorModel videoGen) {
    double _xblockexpression = (double) 0;
    {
      List<List<MediaDescription>> playlists = VideoGenUtils.generatePlaylists(videoGen);
      double minimalSize = VideoGenAnalysisTransformation.getPlayListSize(playlists.get(0));
      for (final List<MediaDescription> playlist : playlists) {
        {
          double playlistSize = VideoGenAnalysisTransformation.getPlayListSize(playlist);
          if ((playlistSize < minimalSize)) {
            minimalSize = playlistSize;
          }
        }
      }
      _xblockexpression = minimalSize;
    }
    return _xblockexpression;
  }
  
  public static double getMaximalSize(final VideoGeneratorModel videoGen) {
    double _xblockexpression = (double) 0;
    {
      double maximalSize = 0.0;
      List<List<MediaDescription>> playlists = VideoGenUtils.generatePlaylists(videoGen);
      for (final List<MediaDescription> playlist : playlists) {
        {
          double playlistSize = VideoGenAnalysisTransformation.getPlayListSize(playlist);
          if ((playlistSize > maximalSize)) {
            maximalSize = playlistSize;
          }
        }
      }
      _xblockexpression = maximalSize;
    }
    return _xblockexpression;
  }
  
  private static double getPlayListSize(final List<MediaDescription> playlist) {
    double _xblockexpression = (double) 0;
    {
      double playlistSize = 0.0;
      for (final MediaDescription video : playlist) {
        {
          final VideoDescription videoDescription = ((VideoDescription) video);
          double _playlistSize = playlistSize;
          double _videoSize = VideoGenUtils.getVideoSize(videoDescription);
          playlistSize = (_playlistSize + _videoSize);
        }
      }
      _xblockexpression = playlistSize;
    }
    return _xblockexpression;
  }
  
  public static double getAverageSize(final VideoGeneratorModel videoGen) {
    double _xblockexpression = (double) 0;
    {
      double averageSize = 0.0;
      List<List<MediaDescription>> playlists = VideoGenUtils.generatePlaylists(videoGen);
      for (final List<MediaDescription> playlist : playlists) {
        {
          double playlistSize = VideoGenAnalysisTransformation.getPlayListSize(playlist);
          double _averageSize = averageSize;
          averageSize = (_averageSize + playlistSize);
        }
      }
      int _size = playlists.size();
      _xblockexpression = (averageSize / _size);
    }
    return _xblockexpression;
  }
  
  public static double getStandardDeviation(final VideoGeneratorModel videoGen) {
    double _xblockexpression = (double) 0;
    {
      double standardDeviation = 0.0;
      List<List<MediaDescription>> playlists = VideoGenUtils.generatePlaylists(videoGen);
      for (final List<MediaDescription> playlist : playlists) {
        double _standardDeviation = standardDeviation;
        double _playListSize = VideoGenAnalysisTransformation.getPlayListSize(playlist);
        double _averageSize = VideoGenAnalysisTransformation.getAverageSize(videoGen);
        double _minus = (_playListSize - _averageSize);
        double _pow = Math.pow(_minus, 2);
        standardDeviation = (_standardDeviation + _pow);
      }
      int _size = playlists.size();
      double _divide = (standardDeviation / _size);
      _xblockexpression = Math.sqrt(_divide);
    }
    return _xblockexpression;
  }
  
  public static double getRealZise(final VideoGeneratorModel videoGen) {
    double _xblockexpression = (double) 0;
    {
      double realSize = 0.0;
      List<List<MediaDescription>> playlists = VideoGenUtils.generatePlaylists(videoGen);
      int playlistIndex = 0;
      for (final List<MediaDescription> playlist : playlists) {
        {
          double _realSize = realSize;
          double _realSize_1 = VideoGenAnalysisTransformation.getRealSize(playlist);
          realSize = (_realSize + _realSize_1);
          playlistIndex++;
        }
      }
      _xblockexpression = realSize;
    }
    return _xblockexpression;
  }
  
  public static double getRealSize(final List<MediaDescription> playlist) {
    File _outPutFoulder = VideoGenConfigs.getOutPutFoulder();
    String _plus = (_outPutFoulder + "/playlists/playlist.mp4");
    return VideoGenUtils.getVideoSize(VideoGenUtils.makePlaylist(playlist, CommonUtils.getOutPutFileName(_plus)));
  }
  
  public static double getRealSize(final String playlistLocation) {
    return VideoGenUtils.getVideoSize(playlistLocation);
  }
}
