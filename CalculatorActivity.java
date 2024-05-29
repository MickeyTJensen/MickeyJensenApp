package com.example.myapp.calculator;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapp.R;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class CalculatorActivity extends AppCompatActivity {

    private TextView textViewResult;
    private boolean lastNumeric;
    private boolean stateError;
    private boolean lastDot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
        this.textViewResult = findViewById(R.id.textViewResult);
    }

    public void onDigit(View v) {
        String value = (String) v.getTag();
        Log.d("CalculatorActivity", "Digit pressed: " + value);

        if (stateError) {
            // Reset the TextView if there's an error
            textViewResult.setText(value);
            stateError = false;
        } else {
            // Append the value to the TextView
            textViewResult.append(value);
        }
        lastNumeric = true;
    }

    public void onDecimalPoint(View v) {
        if (lastNumeric && !stateError && !lastDot) {
            textViewResult.append(".");
            lastNumeric = false;
            lastDot = true;
        }
    }

    public void onOperator(View v) {
        String currentText = textViewResult.getText().toString();
        String value = (String) v.getTag();

        try {
            if (lastNumeric && !stateError) {
                if(value.equals("sqrt")) {
                    double sqrtValue = Math.sqrt(Double.parseDouble(currentText));
                    textViewResult.setText(String.valueOf(sqrtValue));
                } else if(value.equals("%")) {
                    double percentValue = Double.parseDouble(currentText) / 100;
                    textViewResult.setText(String.valueOf(percentValue));
                } else {
                    // For +, -, *, and /
                    textViewResult.append(value);
                }
                lastNumeric = false;
                lastDot = false; // Reset the DOT flag
            }
        } catch (NumberFormatException ex) {
            textViewResult.setText("Error");
            stateError = true;
            lastNumeric = false;
        }
    }

    public void onClear(View v) {
        this.textViewResult.setText("");
        lastNumeric = false;
        stateError = false;
        lastDot = false;
    }

    public void onEqual(View v) {
        if (lastNumeric && !stateError) {
            String txt = textViewResult.getText().toString();
            Expression expression;
            try {
                expression = new ExpressionBuilder(txt).build();
                double result = expression.evaluate();
                textViewResult.setText(Double.toString(result));
                lastDot = true; // Result might contain a decimal
            } catch (ArithmeticException | IllegalArgumentException ex) {
                textViewResult.setText("Error");
                stateError = true;
                lastNumeric = false;
            }
        }
    }

    // ... Additional methods for calculations ...
}