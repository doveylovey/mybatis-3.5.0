/**
 * Copyright 2009-2017 the original author or authors.
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

import org.apache.ibatis.annotations.AutomapConstructor;

import java.io.Serializable;

public class AnnotatedSubject implements Serializable {
    private int id;
    private String name;
    private int age;
    private int height;
    private int weight;

    public AnnotatedSubject() {
    }

    public AnnotatedSubject(int id, String name, int age, int height, int weight) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.height = height;
        this.weight = weight;
    }

    @AutomapConstructor
    public AnnotatedSubject(int id, String name, int age, Integer height, Integer weight) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.height = height == null ? 0 : height;
        this.weight = weight == null ? 0 : weight;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AnnotatedSubject{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", age=").append(age);
        sb.append(", height=").append(height);
        sb.append(", weight=").append(weight);
        sb.append('}');
        return sb.toString();
    }
}
