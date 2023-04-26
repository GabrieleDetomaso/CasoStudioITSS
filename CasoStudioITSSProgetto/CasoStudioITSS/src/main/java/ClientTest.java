import com.google.gson.Gson;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ClientTest {
    public static void main(String[] args) throws IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        Param param = new Param("Ciao", "Tutto", "Ok");
        Gson gson = new Gson();
        String encodedBytes = "";

        File f = new File("C:\\Users\\giaco\\Desktop\\Progetti\\CasoStudioITSS\\CasoStudioITSSProgetto\\CasoStudioITSS\\src\\main\\java\\1-FACE.jpg");

        try {
            try (FileInputStream inputStream = new FileInputStream(f)) {
                encodedBytes = Base64.getEncoder().encodeToString(inputStream.readAllBytes());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        param.setImg(encodedBytes);
        String jsonString = gson.toJson(param);

        try {
            //HttpPost request = new HttpPost("https://deepfacecloudapiunibatesi.azurewebsites.net/represent");
            HttpPost request = new HttpPost("http://127.0.0.1:5000/represent");
            StringEntity stringEntity = new StringEntity(jsonString);
            request.addHeader("content-type", "application/json");
            request.setEntity(stringEntity);

            CloseableHttpResponse response = httpClient.execute(request);
            HttpEntity result = response.getEntity();
            String content = EntityUtils.toString(result, StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.out.print(e.getMessage());
        } finally {
            httpClient.close();
        }
    }


    static class Param {
        String img;
        String username;
        String info;

        public Param(String img, String username, String info) {
            this.img = img;
            this.username = username;
            this.info = info;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }
    }
}

