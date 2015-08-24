package com.jonkoester.circlemeterwidget;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final CircleMeterView circleMeterView = (CircleMeterView) findViewById(R.id.circleMeterWidgetTest);
        final CircleMeterView circlePercentageView = (CircleMeterView) findViewById(R.id.circleMeterWidgetPercent);

        circleMeterView.setActualUnits(24f);
        circleMeterView.setTotalUnits(157f);

        Button plusFiveButton = (Button) findViewById(R.id.plusFiveButton);
        plusFiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                circleMeterView.setActualUnits(circleMeterView.getActualUnits() + 5f);
                circlePercentageView.setActualUnits(circlePercentageView.getActualUnits() + 5f);
            }
        });

        Button minusFiveButton = (Button) findViewById(R.id.minusFiveButton);
        minusFiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                circleMeterView.setActualUnits(circleMeterView.getActualUnits() - 5f);
                circlePercentageView.setActualUnits(circlePercentageView.getActualUnits() - 5f);
            }
        });
    }
}
