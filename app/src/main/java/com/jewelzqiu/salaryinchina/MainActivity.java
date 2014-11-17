package com.jewelzqiu.salaryinchina;

import com.google.gson.JsonObject;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    public static double mRate = 0;

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = (TextView) findViewById(R.id.text_view);
        updateExchangeRate();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_refresh) {
            updateExchangeRate();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        updateContent();
    }

    public void updateExchangeRate() {
        mTextView.setText("获取汇率中...");
        String url
                = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.xchange%20where%20pair%20in(%22JPYCNY%22)&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";
        Ion.with(this).load(url).asJsonObject().setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                if (e != null) {
                    e.printStackTrace();
                    mTextView.setText(e.getMessage());
                    return;
                }
                mRate = result.getAsJsonObject("query").getAsJsonObject("results")
                        .getAsJsonObject("rate").get("Rate").getAsDouble();
                updateContent();
            }
        });
    }

    private void updateContent() {
        if (mRate == 0) {
            updateExchangeRate();
            return;
        }
        StringBuilder builder = new StringBuilder("汇率：\t" + mRate + "\n");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        double salary =
                Double.parseDouble(preferences.getString(getString(R.string.key_salary), "375000"))
                        * mRate;
        double bonus = Double.parseDouble(
                preferences.getString(getString(R.string.key_annual_bonus), "1500000")) * mRate;
        builder.append("税前月薪：\t");
        builder.append(String.format("%.2f", salary));
        builder.append('\n');
        builder.append("税前奖金：\t");
        builder.append(String.format("%.2f", bonus));
        builder.append('\n');
        double housingFundRate =
                Double.parseDouble(preferences.getString(getString(R.string.key_housing_fund), "8"))
                        / 100;
        double insuranceSalary = Math
                .min(Math.max(salary, Util.SOCIAL_SECURITY_MIN), Util.SOCIAL_SECURITY_MAX);
        double insurance = insuranceSalary * (Util.INDIVIDUAL_ENDOWMENT_RATE
                + Util.INDIVIDUAL_MEDICAL_RATE + Util.INDIVIDUAL_UNEMPLOYMENT_RATE);
        double housingFundSalary = Math
                .min(Math.max(salary, Util.HOUSING_FUND_MIN), Util.HOUSING_FUND_MAX);
        double housingFund = housingFundSalary * housingFundRate;
        builder.append("三险一金：\t");
        builder.append(String.format("%.2f", insurance + housingFund));
        builder.append('\n');

        double taxSalary = salary - insurance - housingFund - Util.TAX_FREE_MAX;
        double salaryTax = Util.getSalaryTax(taxSalary);
        builder.append("月薪个税：\t");
        builder.append(String.format("%.2f", salaryTax));
        builder.append('\n');
        builder.append("税后月薪：\t");
        builder.append(String.format("%.2f", taxSalary - salaryTax + Util.TAX_FREE_MAX));
        builder.append('\n');

        double bonusTax = Util.getBonusTax(taxSalary, bonus);
        builder.append("年终奖个税：\t");
        builder.append(String.format("%.2f", bonusTax));
        builder.append('\n');
        builder.append("税后年终奖：\t");
        builder.append(String.format("%.2f", bonus - bonusTax));
        builder.append('\n');

        double total = bonus - bonusTax + (salary - insurance - housingFund - salaryTax) * 12;
        builder.append("全年收入：\t");
        builder.append(String.format("%.2f", total));
        builder.append('\n');
        builder.append("平均每月：\t");
        builder.append(String.format("%.2f", total / 12));
        builder.append('\n');

        mTextView.setText(builder.toString());
    }
}
