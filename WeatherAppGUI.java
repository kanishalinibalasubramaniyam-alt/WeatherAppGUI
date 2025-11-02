import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.swing.*;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

public class WeatherAppGUI extends JFrame {

    private static final String API_KEY = "bac6c1eede1f1a0c03fff4ef1a3ba8a2"; // 🔑 Replace with your OpenWeatherMap API key
    private JTextField cityField;
    private JTextArea resultArea;
    private JButton searchButton;

    public WeatherAppGUI() {
        setTitle("🌦 Weather App");
        setSize(400, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header Panel
        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(70, 130, 180));
        topPanel.setLayout(new FlowLayout());

        JLabel cityLabel = new JLabel("Enter City: ");
        cityLabel.setForeground(Color.WHITE);
        cityLabel.setFont(new Font("Arial", Font.BOLD, 14));

        cityField = new JTextField(15);
        searchButton = new JButton("Get Weather");
        searchButton.setBackground(Color.WHITE);
        searchButton.setForeground(new Color(25, 25, 112));
        searchButton.setFont(new Font("Arial", Font.BOLD, 12));

        topPanel.add(cityLabel);
        topPanel.add(cityField);
        topPanel.add(searchButton);

        // Text Area
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        resultArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(resultArea), BorderLayout.CENTER);

        // Action listener
        searchButton.addActionListener(e -> fetchWeather());
    }

    private void fetchWeather() {
        String city = cityField.getText().trim();
        if (city.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a city name!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String urlString = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + API_KEY + "&units=metric";
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            parseWeatherData(response.toString());
        } catch (Exception ex) {
            resultArea.setText("❌ Error: Unable to fetch weather data.\nCheck your city name or internet connection.");
        }
    }

    private void parseWeatherData(String jsonResponse) {
        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(jsonResponse);

            JSONObject main = (JSONObject) jsonObject.get("main");
            JSONArray weatherArray = (JSONArray) jsonObject.get("weather");
            JSONObject weather = (JSONObject) weatherArray.get(0);
            JSONObject wind = (JSONObject) jsonObject.get("wind");

            String cityName = (String) jsonObject.get("name");
            double temperature = (Double) main.get("temp");
            long humidity = (Long) main.get("humidity");
            String description = (String) weather.get("description");
            double windSpeed = (Double) wind.get("speed");

            resultArea.setText(
                "📍 City: " + cityName +
                "\n🌡 Temperature: " + temperature + " °C" +
                "\n💧 Humidity: " + humidity + "%" +
                "\n🌬 Wind Speed: " + windSpeed + " m/s" +
                "\n☁ Condition: " + description
            );

        } catch (Exception e) {
            resultArea.setText("⚠ Error parsing weather data.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WeatherAppGUI app = new WeatherAppGUI();
            app.setVisible(true);
        });
    }
}
//javac -cp .;json-simple-1.1.1.jar WeatherAppGUI.java
//java -cp .;json-simple-1.1.1.jar WeatherAppGUI