/**
 * Copyright 2009-2018 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ibatis.autoconstructor;

import java.io.Serializable;

public class ExtensiveSubject implements Serializable {
    private byte aByte;
    private short aShort;
    private char aChar;
    private int anInt;
    private long aLong;
    private float aFloat;
    private double aDouble;
    private boolean aBoolean;
    private String aString;
    // enum types
    private TestEnum anEnum;
    // array types

    // string to lob types:
    private String aClob;
    private String aBlob;

    public ExtensiveSubject() {
    }

    public ExtensiveSubject(byte aByte, short aShort, char aChar, int anInt, long aLong, float aFloat, double aDouble, boolean aBoolean, String aString, TestEnum anEnum, String aClob, String aBlob) {
        this.aByte = aByte;
        this.aShort = aShort;
        this.aChar = aChar;
        this.anInt = anInt;
        this.aLong = aLong;
        this.aFloat = aFloat;
        this.aDouble = aDouble;
        this.aBoolean = aBoolean;
        this.aString = aString;
        this.anEnum = anEnum;
        this.aClob = aClob;
        this.aBlob = aBlob;
    }

    public byte getaByte() {
        return aByte;
    }

    public void setaByte(byte aByte) {
        this.aByte = aByte;
    }

    public short getaShort() {
        return aShort;
    }

    public void setaShort(short aShort) {
        this.aShort = aShort;
    }

    public char getaChar() {
        return aChar;
    }

    public void setaChar(char aChar) {
        this.aChar = aChar;
    }

    public int getAnInt() {
        return anInt;
    }

    public void setAnInt(int anInt) {
        this.anInt = anInt;
    }

    public long getaLong() {
        return aLong;
    }

    public void setaLong(long aLong) {
        this.aLong = aLong;
    }

    public float getaFloat() {
        return aFloat;
    }

    public void setaFloat(float aFloat) {
        this.aFloat = aFloat;
    }

    public double getaDouble() {
        return aDouble;
    }

    public void setaDouble(double aDouble) {
        this.aDouble = aDouble;
    }

    public boolean isaBoolean() {
        return aBoolean;
    }

    public void setaBoolean(boolean aBoolean) {
        this.aBoolean = aBoolean;
    }

    public String getaString() {
        return aString;
    }

    public void setaString(String aString) {
        this.aString = aString;
    }

    public TestEnum getAnEnum() {
        return anEnum;
    }

    public void setAnEnum(TestEnum anEnum) {
        this.anEnum = anEnum;
    }

    public String getaClob() {
        return aClob;
    }

    public void setaClob(String aClob) {
        this.aClob = aClob;
    }

    public String getaBlob() {
        return aBlob;
    }

    public void setaBlob(String aBlob) {
        this.aBlob = aBlob;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ExtensiveSubject{");
        sb.append("aByte=").append(aByte);
        sb.append(", aShort=").append(aShort);
        sb.append(", aChar=").append(aChar);
        sb.append(", anInt=").append(anInt);
        sb.append(", aLong=").append(aLong);
        sb.append(", aFloat=").append(aFloat);
        sb.append(", aDouble=").append(aDouble);
        sb.append(", aBoolean=").append(aBoolean);
        sb.append(", aString='").append(aString).append('\'');
        sb.append(", anEnum=").append(anEnum);
        sb.append(", aClob='").append(aClob).append('\'');
        sb.append(", aBlob='").append(aBlob).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public enum TestEnum {
        AVALUE, BVALUE, CVALUE;
    }
}