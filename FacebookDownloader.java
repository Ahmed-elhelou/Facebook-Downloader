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
                    System.out.printf("\rwe found %f urls", count);
                }
            }

        }
        count = 0;
        System.out.println("");

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
                        (count / fullLinks.size() * 100.0), succeeded, failed);
            }

            System.out.printf("\nCongratulations!!\nsucceeded videos: %d || failed videos:%d ", succeeded, failed);

        }

    }

    class FVideo implements Comparable {
        String id;
        String link;
        String downlodLink;
        FPage page;

        public FVideo(String id) {
            this.id = id;
            this.link = createLink();
            this.downlodLink = fetchDownloadLink();
            page = new FPage(fetchPageId());
        }

        public String getId() {
            return this.id;
        }

    public getLink(){
        return this.link;
    }

        public String getDownloadLink() {
            return this.downlodLink;
        }

        @override
        public boolean equals(FVideo other) {
            return this.getId() == other.getId();
        }

        private String createLink() {
            return String.format("%s/videos/%s", this.page.getLink(), this.id);

        }

        private String fetchDownloadLink() {
            URL url = new URL(this.link);
            Scanner input = new Scanner(url.openStream());
            while (input.hasNextLine()) {
                String line = input.nextLine();
                if (line.contains("hd_src:\""))
                    return line.split("hd_src:\"")[1].split("\"")[0];
                else if (line.contains("sd_src:\""))
                    return line.split("sd_src:\"")[1].split("\"")[0];
            }
            return "";
        }

        private String fetchPageId() {
            URL url = new URL(this.link);
            Scanner input = new Scanner(url.openStream());
            while (input.hasNextLine()) {
                String line = input.nextLine();
                if (line.contains("href=\"/"))
                    return line.split("href=\"/")[1].split("/videos/")[0];

            }
            return "";
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