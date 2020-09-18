import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ConfigSync extends JavaPlugin {
    File version = new File(Bukkit.getPluginManager().getPlugin("ConfigShare").getDataFolder().getPath() + "version.yml");
    String path = Bukkit.getPluginManager().getPlugin("ConfigShare").getDataFolder().getPath();
    String versionstr = null;
    String FILE_URL = "http://cyberstorm.duckdns.org/plg.zip";

    @Override
    public void onEnable() {

//Create config or just get current version

        if (!version.exists()) {
            try {
                version.createNewFile();
                String str = "1.0.0";
                BufferedWriter writer = null;
                writer = new BufferedWriter(new FileWriter(version));
                writer.write(str);
                writer.close();
            } catch (IOException e) {
                Bukkit.getLogger().info(e.getMessage());
                return;
            }

            versionstr = "1.0.0";
        } else {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(version));
                StringBuilder content = new StringBuilder();

                int value;
                while ((value = reader.read()) != -1) {
                    content.append((char) value);
                }
                versionstr = content.toString();
            } catch (IOException e) {
                Bukkit.getLogger().info(e.getMessage());
                return;
            }
        }

//Download of config files / file changes

        try {
            InputStream in = null;
            in = new URL(FILE_URL).openStream();
            Files.copy(in, Paths.get(path + "zips/config.zip"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            Bukkit.getLogger().info(e.getMessage());
            return;
        };

//unzip file
        try {
            File destDir = new File(path+"unzip/");
            byte[] buffer = new byte[1024];
            ZipInputStream zis = null;
            zis = new ZipInputStream(new FileInputStream(path + "zips/config.zip"));
            ZipEntry zipEntry = null;
            try {
                zipEntry = zis.getNextEntry();
            } catch (IOException e) {
                Bukkit.getLogger().info(e.getMessage());
            }
            while (zipEntry != null) {
                File newFile = newFile(destDir, zipEntry);
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
        } catch (IOException e) {
            Bukkit.getLogger().info(e.getMessage());
        }
//Get list of all files provided

        List<String> rawfiles = listFiles(path);
        List<String> configfiles = null;

        rawfiles.forEach((temp) -> {
            assert false;
            configfiles.add(temp.replaceAll("/plugins/ConfigShare/zips", ""));
        });
//copy them to the new directory

        for (int i = 0; i < rawfiles.size(); i++) {
            try {
                assert false;
                copy(rawfiles.get(i), configfiles.get(i));
            } catch (IOException e) {
                Bukkit.getLogger().info(e.getMessage());
            }
        }

    }


    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        final Player player = event.getPlayer();

    }



    protected void kickAll() {

        Player[] players = getServer().getOnlinePlayers().toArray(new Player[0]);

        for (Player player : players) {
            player.kickPlayer(getConfig().getString("kickreason"));
        }
    }

    protected void Shutdown() {
        kickAll();
        Bukkit.getServer().savePlayers();
        Server server = Bukkit.getServer();

        server.savePlayers();

        for (World world : server.getWorlds()) {
            world.save();
            server.unloadWorld(world, true);
        }

        server.shutdown();
    }

    public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }

    public static void copy(String start, String dest) throws IOException{
        Path copied = Paths.get(dest);
        Path originalPath = Paths.get(start);
        Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);
    }

    public static List<String> listFiles(String startDir) {
        List<String> allfiles = null;
        try (Stream<Path> walk = Files.walk(Paths.get("D:/Programming"))) {
            // We want to find only regular files
            List<String> result = walk.filter(Files::isRegularFile)
                    .map(Path::toString).collect(Collectors.toList());

            assert false;
            allfiles.addAll(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return allfiles;
    }
}
