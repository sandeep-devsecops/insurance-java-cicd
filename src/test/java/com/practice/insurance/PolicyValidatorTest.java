package com.practice.insurance;
import org.junit.Test;import static org.junit.Assert.*;
public class PolicyValidatorTest {
 @Test public void valid(){assertTrue(PolicyValidator.valid("Alex","Health",1500));}
 @Test public void invalidPremium(){assertFalse(PolicyValidator.valid("Alex","Life",-1));}
 @Test public void invalidName(){assertFalse(PolicyValidator.valid(" ","Vehicle",200));}
}