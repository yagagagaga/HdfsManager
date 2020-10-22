package hdfsmanager.util;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/**
 * <pre>
 * ResourcesDepository, 资源仓库类。
 *
 * 本类用来获取可种资源，现在可通过本类获取的资源有图像、图标、声音等。
 *
 * 本类使用缓冲来保存使用过的资源，对于下一次对资源的调用将会首先从缓冲中查找，
 * 对于有重复使用资源的情况，本类将大大提高资源访问速度，减少IO或网络访问次数。
 *
 * 对于同一资源在缓冲中只保存一次。
 *
 * 本类的所有方法都是静态方法，对于一个程序来说，只有一个缓冲区。用户可以不用
 * 生成本类的实例来访问资源。
 *
 * 由于资源是大家共用的，请确保对资源的使用不影响其它用户和对象。
 * </pre>
 *
 * Created on 2006-11-15
 */
public final class ResourcesDepository {
	private static final String LOG_STRING = "Can't find the sound: ";
	private static final Logger log = Logger.getLogger(ResourcesDepository.class);

	private ResourcesDepository() {
		throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
	}

	/**
	 * <pre>
	 * 获取图像资源。
	 *
	 * 首先查看本地是否有图像资源名称所指定的文件，如果有则加载此文件，
	 * 否则当作URL加载。
	 * </pre>
	 *
	 * @param name 图像资源名称。
	 * @return 图像资源。
	 * @see #getImage(File file)
	 * @see #getImage(URL imageURL)
	 */
	public static Image getImage(final String name) {

		// 查找缓冲中是否有该资源
		if (images.containsKey(name)) {
			// 有就直接返回
			return images.get(name);
		}
		// 先判断本地是否存在指定名称的文件
		try {
			File file = new File(name);
			if (file.exists()) {
				// 有就加载文件
				Image image = getImage(file);
				if (image != null) {
					images.put(name, image);
					return image;
				}
			}
		} catch (Exception e) {
			log.error(e);
		}
		// 没有或加载不成功就当成资源下载
		URL url = ResourcesDepository.class.getResource(name);
		if (url == null) {
			url = ResourcesDepository.class.getResource("/" + name);
		}
		Image image = getImage(url);
		if (image != null) {
			images.put(name, image);
		}
		return image;
	}

	/**
	 * 获取图像资源。
	 *
	 * @param file 图像资源文件。
	 * @return 图像资源。
	 * @see #getImage(String name)
	 * @see #getImage(URL imageURL)
	 */
	public static Image getImage(final File file) {

		// 查找缓冲中是否有该资源
		if (images.containsKey(file.getAbsolutePath())) {
			// 有就直接返回
			return images.get(file.getAbsolutePath());
		}
		try {
			Image image = ImageIO.read(file);
			if (image != null) {
				images.put(file.getAbsolutePath(), image);
			}
			return image;
		} catch (IOException e) {
			log.error(e);
		}
		return null;
	}

	/**
	 * <pre>
	 * 获取图像资源。
	 *
	 * 使用本方法获取资源，该资源必须位于类路径之下。
	 * </pre>
	 *
	 * @param url 图像资源URL。
	 * @return 图像资源。
	 * @see #getImage(File file)
	 * @see #getImage(String name)
	 */
	public static Image getImage(final URL url) {

		// 查找缓冲中是否有该资源。
		if (images.containsKey(url.toString())) {
			// 有就直接返回
			return images.get(url.toString());
		}
		try {
			Image image = ImageIO.read(url);
			if (image != null) {
				images.put(url.toString(), image);
			}
			return image;
		} catch (IOException e) {
			log.error(e);
		}
		return null;
	}

	/**
	 * <pre>
	 * 获取图标资源。
	 *
	 * 使用本方法获取资源，该资源必须位于类路径之下。
	 * </pre>
	 *
	 * @param name 图标资源名称，必须是图像文件。
	 * @return 图标资源。
	 * @see #getIcon(File file)
	 * @see #getIcon(URL iconURL)
	 * @see #getImage(String name)
	 */
	public static Icon getIcon(final String name) {

		// 查找缓冲中是否有该资源。
		if (icons.containsKey(name)) {
			// 有就直接返回
			return icons.get(name);
		}
		// 先判断本地是否存在指定名称的文件
		try {
			File file = new File(name);
			if (file.exists()) {
				// 有就加载文件
				Icon icon = getIcon(file);
				if (icon != null) {
					icons.put(name, icon);
					return icon;
				}
			}
		} catch (Exception e) {
			log.error(e);
		}
		// 没有或加载不成功就当成资源下载
		URL url = ResourcesDepository.class.getResource(name);
		if (url == null) {
			url = ResourcesDepository.class.getResource("/" + name);
		}
		Icon icon = getIcon(url);
		if (icon != null) {
			icons.put(name, icon);
		}
		return icon;
	}

	/**
	 * 获取图标资源。
	 *
	 * @param file 图标资源文件，必须是图像文件。
	 * @return 返回图标资源。
	 * @see #getIcon(String name)
	 * @see #getIcon(URL iconURL)
	 * @see #getImage(File file)
	 */
	public static Icon getIcon(final File file) {

		// 查找缓冲中是否有该资源。
		if (icons.containsKey(file.getAbsolutePath())) {
			// 有就直接返回
			return icons.get(file.getAbsolutePath());
		}
		Icon icon = new ImageIcon(file.getAbsolutePath());
		icons.put(file.getAbsolutePath(), icon);
		return icon;
	}

	/**
	 * 获取系统图标
	 */
	public static Icon getSysIcon(String fileName, boolean isDir) {
		if (isDir) {
			Icon ret = icons.get("dir");
			if (ret == null) {
				File file = FileUtils.getTempDirectory();
				icons.put("dir", FileSystemView.getFileSystemView().getSystemIcon(file));
			}
			return icons.get("dir");
		}
		if (fileName == null || fileName.length() == 0) {
			return null;
		}

		String ext = getSuffix(fileName);
		try {
			Icon ret = icons.get(ext);
			if (ret == null) {
				File file = File.createTempFile("icon", "." + ext);
				icons.put(ext, FileSystemView.getFileSystemView().getSystemIcon(file));
				FileUtils.deleteQuietly(file);
			}
			return icons.get(ext);
		} catch (IOException e) {
			log.info("获取系统图标失败，失败原因:" + e.getMessage());
			return null;
		}
	}

	private static String getSuffix(final String fileName) {

		String ext = "unknown";
		if (fileName.contains(".") &&
				fileName.lastIndexOf('.') != fileName.length() - 1) {
			ext = fileName.substring(fileName.lastIndexOf('.') + 1);
			if (ext.contains(":") || ext.contains("/"))
				return "unknown";
		}
		return ext;
	}

	/**
	 * <pre>
	 * 获取图标资源。
	 *
	 * 使用本方法获取资源，该资源必须位于类路径之下。
	 * </pre>
	 *
	 * @param url 图标资源URL，必须是图像文件。
	 * @return 图标资源。
	 * @see #getIcon(File file)
	 * @see #getIcon(String name)
	 * @see #getImage(URL iconURL)
	 */
	public static Icon getIcon(final URL url) {

		// 查找缓冲中是否有该资源。
		if (icons.containsKey(url.toString())) {
			// 有就直接返回
			return icons.get(url.toString());
		}
		Icon icon = new ImageIcon(url);
		icons.put(url.toString(), icon);
		return icon;
	}

	/**
	 * <pre>
	 * 获取声音资源。
	 *
	 * 声音资源可能是AudioInputStream, Sequencer或URL对象之一。
	 * 波形文件将得到AudioInputStream对象，MIDI文件将得到Sequencer对象，
	 * rmf文件将得到URL对象。
	 * </pre>
	 *
	 * @param file 声音资源文件。
	 * @return 声音资源，是AudioInputStream, Sequencer或URL对象之一。
	 * @see #getSound(String name)
	 * @see #getSound(URL soundURL)
	 */
	@SuppressWarnings("deprecation")
	public static Object getSound(final File file) {
		Object sound = null;
		URL soundURL = null;

		try {
			soundURL = file.toURI().toURL();
			sound = getSound(soundURL);
			return sound;
		} catch (MalformedURLException ex) {
			log.info(LOG_STRING + soundURL);
		}

		return sound;
	}

	/**
	 * <pre>
	 * 获取声音资源。
	 *
	 * 使用本方法获取资源，该资源必须位于类路径之下。
	 *
	 * 声音资源可能是AudioInputStream, Sequencer或URL对象之一。
	 * 波形文件将得到AudioInputStream对象，MIDI文件将得到Sequencer对象，
	 * rmf文件将得到URL对象。
	 * </pre>
	 * </pre>
	 *
	 * @param name 声音资源名称。
	 * @return 声音资源，是AudioInputStream, Sequencer或URL对象之一。
	 * @see #getSound(File file)
	 * @see #getSound(URL soundURL)
	 */
	public static Object getSound(final String name) {

		URL soundURL = ClassLoader.getSystemResource(name);
		if (soundURL == null) {
			log.info(LOG_STRING + name);
			return null;
		}
		return getSound(soundURL);
	}

	/**
	 * <pre>
	 * 获取声音资源。
	 *
	 * 使用本方法获取资源，该资源必须位于类路径之下。
	 *
	 * 声音资源可能是AudioInputStream, Sequencer或URL对象之一。
	 * 波形文件将得到AudioInputStream对象，MIDI文件将得到Sequencer对象，
	 * rmf文件将得到URL对象。
	 * </pre>
	 * </pre>
	 *
	 * @param soundURL 声音资源URL。
	 * @return 声音资源，是AudioInputStream, Sequencer或URL对象之一。
	 * @see #getSound(File file)
	 * @see #getSound(String name)
	 */
	public static Object getSound(final URL soundURL) {

		Object sound;
		String name = soundURL.toString();
		// 判断缓冲中是否有该资源。
		if (!sounds.containsKey(name)) {
			// 没有则创建该资源。
			sound = createSound(soundURL);
		} else {
			// 有则直接取。
			sound = sounds.get(name);
		}
		if (sound == null) {
			return null;
		}
		// 如果声音资源是AudioInputStream的话，重设这个流。
		if (sound instanceof AudioInputStream) {
			try {
				((AudioInputStream) sound).reset();
			} catch (IOException ex) {
				log.error(ex);
			}
		}
		return sound;
	}

	/**
	 * <pre>
	 * 创建声音资源。
	 *
	 * 声音资源可能是AudioInputStream, Sequencer或URL对象之一。
	 * 波形文件将得到AudioInputStream对象，MIDI文件将得到Sequencer对象，
	 * rmf文件将得到URL对象。
	 * </pre>
	 *
	 * @param soundURL 声音资源URL。
	 * @return 声音资源，是AudioInputStream, Sequencer或URL对象之一。
	 */
	private static Object createSound(final URL soundURL) {
		Object sound = null;
		try {
			AudioInputStream ais;
			ais = AudioSystem.getAudioInputStream(soundURL);
			AudioFormat format = ais.getFormat();
			/**
			 * we can't yet open the device for ALAW/ULAW playback, convert ALAW/ULAW to PCM
			 */
			if ((format.getEncoding() == AudioFormat.Encoding.ULAW) ||
					(format.getEncoding() == AudioFormat.Encoding.ALAW)) {
				AudioFormat tmp = new AudioFormat(
						AudioFormat.Encoding.PCM_SIGNED,
						format.getSampleRate(),
						format.getSampleSizeInBits() * 2,
						format.getChannels(),
						format.getFrameSize() * 2,
						format.getFrameRate(),
						true);
				ais = AudioSystem.getAudioInputStream(tmp, ais);
				format = tmp;
			}
			ais.mark((int) ais.getFrameLength() * format.getFrameSize());
			sound = ais;
			// Don't convert the ais to clip or line, since the count of
			// the clip or line of the mixer is limited. Converting the ais
			// to clip or line leave to the ZSoundPlayer.
		} catch (IOException ex1) {
			log.info(LOG_STRING + soundURL);
			return null;
		} catch (UnsupportedAudioFileException ex2) {
			try {
				sound = MidiSystem.getSequence(soundURL);
			} catch (IOException ex3) {
				log.info(LOG_STRING + soundURL);
				return null;
			} catch (InvalidMidiDataException ex4) {
				// Dont open the sound and make this sound to
				// BufferedInputStream, since the stream of sound will be
				// closed by the sequencer. Opening the sound leave to
				// the ZSoundPlayer.
				sound = soundURL;
			}
		}
		if (sound != null) {
			sounds.put(soundURL.toString(), sound);
		} else {
			log.info("Unsupported audio file: " + soundURL);
		}
		return sound;
	}

	/**
	 * 图像缓冲。
	 */
	private static HashMap<String, Image> images = new HashMap<>();

	/**
	 * 图标缓冲。
	 */
	private static HashMap<String, Icon> icons = new HashMap<>();

	/**
	 * 声音缓冲。
	 */
	private static HashMap<String, Object> sounds = new HashMap<>();
}
