package helpers;

import configs.VideoGenConfigs;
import helpers.ProcessHelper;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.xtext.example.mydsl.videoGen.BlackWhiteFilter;
import org.xtext.example.mydsl.videoGen.Filter;
import org.xtext.example.mydsl.videoGen.FlipFilter;
import org.xtext.example.mydsl.videoGen.MediaDescription;
import org.xtext.example.mydsl.videoGen.NegateFilter;
import org.xtext.example.mydsl.videoGen.VideoDescription;
import utils.CommonUtils;

@SuppressWarnings("all")
public class FFMPEGHelper {
  public static String generateThumbnail(final String videoLocation) {
    String _xblockexpression = null;
    {
      final ArrayList<String> command = new ArrayList<String>();
      command.add("ffmpeg");
      command.add("-y");
      command.add("-i");
      command.add(videoLocation);
      command.add("-r");
      command.add("1");
      command.add("-t");
      command.add("00:00:01");
      command.add("-ss");
      double _random = Math.random();
      int _round = Math.round(Float.parseFloat(FFMPEGHelper.getVideoDurationString(videoLocation).split(":")[2]));
      double _multiply = (_random * _round);
      String _plus = ("00:00:0" + Double.valueOf(_multiply));
      command.add(_plus);
      command.add("-f");
      command.add("image2");
      File _outPutFoulder = VideoGenConfigs.getOutPutFoulder();
      String _plus_1 = (_outPutFoulder + "/thumbs/thumb.png");
      String outputFile = CommonUtils.getOutPutFileName(_plus_1);
      command.add(outputFile);
      ProcessHelper.execute(command);
      _xblockexpression = outputFile;
    }
    return _xblockexpression;
  }
  
  public static String concatVideos(final List<MediaDescription> medias, final String outputfilename) {
    final ArrayList<String> command = new ArrayList<String>();
    String filter = "";
    command.add("ffmpeg");
    command.add("-y");
    int i = 0;
    for (final MediaDescription media : medias) {
      {
        command.add("-i");
        if ((media instanceof VideoDescription)) {
          final VideoDescription vdescription = ((VideoDescription) media);
          Filter _filter = vdescription.getFilter();
          if ((_filter instanceof BlackWhiteFilter)) {
            FFMPEGHelper.applyFilter("format=gray", vdescription);
          }
          Filter _filter_1 = vdescription.getFilter();
          if ((_filter_1 instanceof NegateFilter)) {
            FFMPEGHelper.applyFilter("negate", vdescription);
          }
          Filter _filter_2 = vdescription.getFilter();
          if ((_filter_2 instanceof FlipFilter)) {
            Filter _filter_3 = vdescription.getFilter();
            final FlipFilter flipFilter = ((FlipFilter) _filter_3);
            String flipOrientation = flipFilter.getOrientation();
            boolean _equals = flipOrientation.equals("horizontal");
            if (_equals) {
              flipOrientation = "h";
            }
            boolean _equals_1 = flipOrientation.equals("vertical");
            if (_equals_1) {
              flipOrientation = "v";
            }
            FFMPEGHelper.applyFilter((flipOrientation + "flip"), vdescription);
          }
        }
        command.add(media.getLocation());
        if (((((media.getLocation().replace(".", "@").split("@")[1].equals("jpg") || 
          media.getLocation().replace(".", "@").split("@")[1].equals("png")) || 
          media.getLocation().replace(".", "@").split("@")[1].equals("gif")) || 
          media.getLocation().replace(".", "@").split("@")[1].equals("bpm")) || 
          media.getLocation().replace(".", "@").split("@")[1].equals("tiff"))) {
          String _filter_4 = filter;
          filter = (_filter_4 + (("[" + Integer.valueOf(i)) + ":v:0][0]"));
        } else {
          String _filter_5 = filter;
          filter = (_filter_5 + (((("[" + Integer.valueOf(i)) + ":v:0][") + Integer.valueOf(i)) + ":a:0]"));
        }
        i++;
      }
    }
    command.add("-filter_complex");
    String _filter = filter;
    filter = (_filter + (("concat=n=" + Integer.valueOf(i)) + ":v=1:a=1[outv][outa]"));
    command.add(filter);
    command.add("-map");
    command.add("[outv]");
    command.add("-map");
    command.add("[outa]");
    command.add("-strict");
    command.add("-2");
    command.add(outputfilename);
    ProcessHelper.execute(command);
    return outputfilename;
  }
  
  public static int[] getVideoResolution(final String videoFile) {
    final ArrayList<String> command = new ArrayList<String>();
    command.add("ffmpeg");
    command.add("-i");
    command.add(videoFile);
    final String[] IO = ProcessHelper.executeAndGetIOStream(command);
    final ArrayList<Integer> resolution = new ArrayList<Integer>();
    for (final String line : IO) {
      boolean _contains = line.contains("Stream #0:0");
      if (_contains) {
        Pattern pattern = Pattern.compile(",\\s[0-9]+x[0-9]+");
        Matcher matcher = pattern.matcher(line);
        boolean _find = matcher.find();
        if (_find) {
          final String[] res = matcher.group(0).replace(", ", "").split("x");
          resolution.add(Integer.valueOf(Integer.parseInt(res[0])));
          resolution.add(Integer.valueOf(Integer.parseInt(res[1])));
        }
      }
    }
    return ((int[])Conversions.unwrapArray(resolution, int.class));
  }
  
  public static String homogenizeMediaResolution(final String filename, final int inputWidth, final int inputHeight, final int outputWidth, final int outputHeight) {
    if (((inputWidth == outputWidth) && (inputHeight == outputHeight))) {
      return filename;
    }
    String[] file = filename.replace(".", "@#").split("@#");
    ArrayList<String> command = new ArrayList<String>();
    command.add("ffmpeg");
    command.add("-i");
    command.add(filename);
    command.add("-strict");
    command.add("-2");
    command.add("-vf");
    if (((((file[1].equals("jpg") || file[1].equals("png")) || file[1].equals("bpm")) || file[1].equals("tiff")) || file[1].equals("gif"))) {
      command.add(((("scale=" + Integer.valueOf(outputWidth)) + ":") + Integer.valueOf(outputHeight)));
    } else {
      command.add(((((((("pad=" + Integer.valueOf(outputWidth)) + ":") + Integer.valueOf(outputHeight)) + ":") + Integer.valueOf(((outputWidth - inputWidth) / 2))) + ":") + Integer.valueOf(((outputHeight - inputHeight) / 2))));
    }
    File _outPutFoulder = VideoGenConfigs.getOutPutFoulder();
    String _plus = (_outPutFoulder + "/resizes/");
    String _get = IterableExtensions.<String>last(((Iterable<String>)Conversions.doWrapArray(new File(filename).getAbsolutePath().replace("\\", "/").split("/")))).replace(".", "@").split("@")[0];
    String _plus_1 = (_plus + _get);
    String _plus_2 = (_plus_1 + "_o.");
    String _get_1 = file[1];
    String _plus_3 = (_plus_2 + _get_1);
    final String outputFile = CommonUtils.getOutPutFileName(_plus_3);
    command.add(outputFile);
    ProcessHelper.execute(command);
    return outputFile;
  }
  
  public static int getVideoDuration(final String videoLocation) {
    int _xblockexpression = (int) 0;
    {
      final ArrayList<String> command = new ArrayList<String>();
      command.add("ffmpeg");
      command.add("-i");
      command.add(videoLocation);
      final String[] IO = ProcessHelper.executeAndGetIOStream(command);
      int duration = 0;
      for (final String line : IO) {
        boolean _contains = line.contains("Duration:");
        if (_contains) {
          final String stringduration = line.trim().split(",")[0].split(" ")[1];
          int _round = Math.round(Float.parseFloat(stringduration.split(":")[0]));
          int _multiply = (3600 * _round);
          int _round_1 = Math.round(Float.parseFloat(stringduration.split(":")[1]));
          int _multiply_1 = (60 * _round_1);
          int _plus = (_multiply + _multiply_1);
          int _round_2 = Math.round(Float.parseFloat(stringduration.split(":")[2]));
          int _plus_1 = (_plus + _round_2);
          duration = _plus_1;
        }
      }
      _xblockexpression = duration;
    }
    return _xblockexpression;
  }
  
  public static String getVideoDurationString(final String videoLocation) {
    String _xblockexpression = null;
    {
      final ArrayList<String> command = new ArrayList<String>();
      command.add("ffmpeg");
      command.add("-i");
      command.add(videoLocation);
      final String[] IO = ProcessHelper.executeAndGetIOStream(command);
      String duration = "";
      for (final String line : IO) {
        boolean _contains = line.contains("Duration:");
        if (_contains) {
          duration = line.trim().split(",")[0].split(" ")[1];
        }
      }
      _xblockexpression = duration;
    }
    return _xblockexpression;
  }
  
  public static String videoToGif(final String videoLocation, final int width, final int height) {
    String _xblockexpression = null;
    {
      final ArrayList<String> command = new ArrayList<String>();
      String[] file = videoLocation.replace(".", "@#").split("@#");
      command.add("ffmpeg");
      command.add("-i");
      command.add(videoLocation);
      File _outPutFoulder = VideoGenConfigs.getOutPutFoulder();
      String _plus = (_outPutFoulder + "/gifs/");
      String _get = IterableExtensions.<String>last(((Iterable<String>)Conversions.doWrapArray(new File(videoLocation).getAbsolutePath().replace("\\", "/").split("/")))).replace(".", "@").split("@")[0];
      String _plus_1 = (_plus + _get);
      String _plus_2 = (_plus_1 + ".gif");
      final String outputFile = CommonUtils.getOutPutFileName(_plus_2);
      command.add("-vf");
      command.add(((("scale=" + Integer.valueOf(width)) + ":") + Integer.valueOf(height)));
      command.add(outputFile);
      command.add("-hide_banner");
      ProcessHelper.execute(command);
      _xblockexpression = outputFile;
    }
    return _xblockexpression;
  }
  
  public static String applyFilter(final String filter, final MediaDescription media) {
    String _xblockexpression = null;
    {
      final ArrayList<String> filtercommand = new ArrayList<String>();
      filtercommand.add("ffmpeg");
      filtercommand.add("-i");
      filtercommand.add(media.getLocation());
      filtercommand.add("-strict");
      filtercommand.add("-2");
      filtercommand.add("-vf");
      filtercommand.add(filter);
      File _outPutFoulder = VideoGenConfigs.getOutPutFoulder();
      String _plus = (_outPutFoulder + "/filtered/");
      String _location = media.getLocation();
      String _last = IterableExtensions.<String>last(((Iterable<String>)Conversions.doWrapArray(new File(_location).getAbsolutePath().replace("\\", "/").split("/"))));
      String _plus_1 = (_plus + _last);
      String outputFile = CommonUtils.getOutPutFileName(_plus_1);
      filtercommand.add(outputFile);
      ProcessHelper.execute(filtercommand);
      media.setLocation(outputFile);
      _xblockexpression = outputFile;
    }
    return _xblockexpression;
  }
}
