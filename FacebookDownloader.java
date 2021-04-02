import java.io.File;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class FacebookDownloader {
    public static void main(String[] args) throws Exception {
        File bodyHtml = new File("bodyHtml.txt");
        File linksFile = new File("Failed Links.txt");
        File fullLinksFile = new File("Full Links File.txt");
        File FinalinksFile = new File("Succeeded Links.txt");
        Scanner input = new Scanner(bodyHtml, "UTF-8");

        String currentVidId;

        ArrayList<FVideo> videosList = new ArrayList<>();
        ArrayList<FPage> pagesList = new ArrayList<>();
        ArrayList<FVideo> testList = new ArrayList<>();
        // testList.add(new FVideo("5"));
        // testList.add(new FVideo("2"));
        // System.out.println(testList.contains(new FVideo("9")));

        double count = 0;
        int succeeded = 0;
        int failed = 0;

        while (input.hasNext()) {
            String currentNext = input.next();

            if (currentNext.contains("/videos/")) {
                String[] splitString = currentNext.split("/");
                currentVidId = splitString[3];
                currentVidId = currentVidId.substring(0, currentVidId.length() - 1);
                FVideo currentVideo = new FVideo(currentVidId);

                if (!videosList.contains(currentVideo)) {

                    videosList.add(currentVideo);
                    pagesList.add(currentVideo.page);
                    count++;
                    System.out.printf("\rwe found %1.0f urls", count);
                }
            }

        }
        count = 0;

        Collections.sort(videosList);
        // ArrayList<String> finalLinks = new ArrayList<>();
        try (PrintWriter outLinksFile = new PrintWriter(linksFile);
                PrintWriter outFinalLinksFile = new PrintWriter(FinalinksFile);
                PrintWriter outFukkLinksFile = new PrintWriter(fullLinksFile);) {
            for (FVideo fVideo : videosList) {
                outFukkLinksFile.println(fVideo.getLink());
                if (fVideo.getDownloadLink().equals("")) {
                    failed++;
                    outLinksFile.println(fVideo.getLink());
                } else {
                    succeeded++;
                    outFinalLinksFile.println(fVideo.getDownloadLink());
                }
                count++;
                System.out.printf("\rCompleted: %2.2f%% || succeeded videos: %2d || failed videos:%2d",
                        (count / videosList.size() * 100.0), succeeded, failed);
            }

            System.out.printf("\nCongratulations!!\nsucceeded videos: %d || failed videos:%d ", succeeded, failed);

        }

    }
}

class FVideo implements Comparable<FVideo> {
    String id;
    String link;
    String downlodLink;
    FPage page;

    public FVideo(String id) {
        this.id = id;
        page = new FPage(fetchPageId());
        this.link = createLink();
        this.downlodLink = fetchDownloadLink();
    }

    public String getId() {
        return this.id;
    }

    public String getLink() {
        return this.link;
    }

    public String getDownloadLink() {
        return this.downlodLink;
    }

    @Override
    public boolean equals(Object o) {
        FVideo other = (FVideo) o;
        return this.id.equals(other.getId());
    }

    @Override
    public String toString() {
        return this.id;
    }

    private String createLink() {

        return String.format("https://www.facebook.com/%s", this.id);

    }

    private String fetchDownloadLink() {
        try {
            URL url = new URL(this.link);
            Scanner input = new Scanner(url.openStream());
            while (input.hasNextLine()) {
                String line = input.nextLine();
                if (line.contains("hd_src:\"")) {

                    input.close();
                    return line.split("hd_src:\"")[1].split("\"")[0];
                } else if (line.contains("sd_src:\"")) {
                    input.close();
                    return line.split("sd_src:\"")[1].split("\"")[0];
                }
            }
        } catch (Exception e) {
            e.getStackTrace();
        }

        return "";
    }

    private String fetchPageId() {
        try {
            URL url = new URL(this.link);
            Scanner input = new Scanner(url.openStream());
            while (input.hasNextLine()) {
                String line = input.nextLine();
                if (line.contains("href=\"/")) {

                    return line.split("href=\"/")[1].split("/videos/")[0];
                }

            }
        } catch (Exception e) {
            e.getStackTrace();
        }

        return "104890151047099";
    }

    public int compareTo(FVideo o) {
        return this.id.compareTo(o.getId());
    }
}

class FPage {
    String id;
    String link;

    public FPage(String id) {
        this.id = id;
        this.link = createPageLink();
    }

    public String getLink() {
        return this.link;
    }

    public String getId() {
        return this.id;
    }

    private String createPageLink() {
        return String.format("https://www.facebook.com/%s", this.id);
    }
}