package org.concordion.internal.listener;

import org.concordion.api.listener.ExampleEvent;
import org.concordion.api.listener.ExampleListener;
import org.concordion.api.listener.OuterExampleEvent;
import org.concordion.api.listener.OuterExampleListener;

public class FixtureExampleHook implements OuterExampleListener, ExampleListener {
   @Override
   public void beforeOuterExample(OuterExampleEvent event) {
       event.getFixture().beforeExample(event.getExampleName());
   }

   @Override
   public void afterOuterExample(OuterExampleEvent event) {
       event.getFixture().afterExample(event.getExampleName());
   }
   
   @Override
   public void beforeExample(ExampleEvent event) {
       event.getFixture().beforeExample(event.getExampleName());
   }

   @Override
   public void afterExample(ExampleEvent event) {
       event.getFixture().afterExample(event.getExampleName());
   }
}