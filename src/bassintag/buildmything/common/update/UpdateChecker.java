package bassintag.buildmything.common.update;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import bassintag.buildmything.common.BuildMyThing;

public class UpdateChecker {
	
	public static boolean isOutdated(BuildMyThing plugin){
		String version = plugin.getDescription().getVersion();
		version = "v" + version;
		version = version.trim();
		
		String onlineVersion = getLastVersion("http://dev.bukkit.org/bukkit-plugins/build-my-thing/files.rss");
		int index = onlineVersion.indexOf(" v");
		onlineVersion = onlineVersion.substring(index);
		onlineVersion = onlineVersion.trim();
		
		if(version.equalsIgnoreCase(onlineVersion)){
			return false;
		}
		
		return true;
	}
	
	public static String getLastVersion(String URL){
		try {
			URL readURL = new URL(URL);
			BufferedReader bf = new BufferedReader(new InputStreamReader(readURL.openStream()));
			String version = "";
			String line;
			String lastLine = "";
			while((line = bf.readLine()) != null){
				if(lastLine.contains("<item>")){
					if(line.contains("<title>")){
						int firstPos = line.indexOf("<title>");
						String temp = line.substring(firstPos);
						temp = temp.replace("<title>", "");
						int lastPos = temp.indexOf("</title>");
						temp = temp.substring(0, lastPos);
						temp = temp.replace("</title>", "");
						version = temp;
						break;
					}
				}
				lastLine = line;
			}
			bf.close();
			return version;
		} catch(Exception ex){
			ex.printStackTrace();
		}
		return "";
	}
}
