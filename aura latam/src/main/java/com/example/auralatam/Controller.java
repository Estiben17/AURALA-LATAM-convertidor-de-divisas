package com.example.auralatam;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Controller {

    @FXML
    private TextField amountTextField; // TextField para la cantidad a convertir

    @FXML
    private TextField convertedAmountTextField; // TextField para mostrar la cantidad convertida

    @FXML
    private ComboBox<String> toComboBox; // ComboBox para seleccionar la moneda a la cual convertir

    @FXML
    private ComboBox<String> toComboBox1; // ComboBox para seleccionar el valor de la divisa

    @FXML
    private Button convertButton; // Botón para realizar la conversión

    private final String apiUrl = "https://v6.exchangerate-api.com/v6/bd2689d9009d0339a981d323/latest/USD";

    private List<Double> conversionValues = new ArrayList<>();
    private double CONVER = 0.0; // Variable para almacenar el valor de conversión de COP
    private final List<String> targetCurrencies = List.of("CAD", "RUB", "EUR", "USD", "COP", "BRL", "MXN");

    @FXML
    public void initialize() {
        fetchExchangeRates();

        // Agregar listener al ComboBox toComboBox para actualizar toComboBox1
        toComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            updateCurrencyValues(newValue);
        });

        // Agregar acción al botón convertButton
        convertButton.setOnAction(event -> convertCurrency());
    }

    private void fetchExchangeRates() {
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JsonObject jsonObject = JsonParser.parseString(response.toString()).getAsJsonObject();
            JsonObject rates = jsonObject.getAsJsonObject("conversion_rates");

            // Obtener las monedas del JSON y guardar los valores de conversión solo para las monedas especificadas
            List<String> currencies = new ArrayList<>();
            for (Map.Entry<String, JsonElement> entry : rates.entrySet()) {
                String currency = entry.getKey();
                if (targetCurrencies.contains(currency)) {
                    currencies.add(currency);

                    // Guardar el valor de conversión
                    double value = entry.getValue().getAsDouble();
                    conversionValues.add(value);

                    // Guardar el valor de conversión de COP en CONVER
                    if (currency.equals("COP")) {
                        CONVER = value;
                    }
                }
            }

            // Mostrar las monedas en el ComboBox toComboBox
            toComboBox.setItems(FXCollections.observableArrayList(currencies));

            // Cerrar la conexión
            connection.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateCurrencyValues(String selectedCurrency) {
        // Obtener el índice correspondiente al ComboBox toComboBox
        int index = toComboBox.getItems().indexOf(selectedCurrency);

        if (index >= 0 && index < conversionValues.size()) {
            double selectedValue = conversionValues.get(index);

            // Construir una lista con el valor de conversión seleccionado sin la moneda y los dos puntos
            List<String> currencyValues = new ArrayList<>();
            currencyValues.add(String.valueOf(selectedValue));

            // Mostrar los valores de conversión en el ComboBox toComboBox1
            toComboBox1.setItems(FXCollections.observableArrayList(currencyValues));
        } else {
            System.out.println("No se encontró el valor de conversión para la moneda seleccionada: " + selectedCurrency);
        }
    }

    private void convertCurrency() {
        try {
            // Obtener el valor ingresado en amountTextField
            double amount = Double.parseDouble(amountTextField.getText());

            // Obtener el valor de conversión seleccionado en toComboBox1
            double conversionRate = Double.parseDouble(toComboBox1.getValue());

            // Realizar la conversión de COP a la moneda seleccionada (USD)
            double convertedAmount = amount / CONVER * conversionRate;

            // Mostrar el resultado de la conversión en convertedAmountTextField
            convertedAmountTextField.setText(String.format("%.2f", convertedAmount));

        } catch (NumberFormatException e) {
            System.out.println("Error: Por favor ingrese un valor válido en el campo de cantidad.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
