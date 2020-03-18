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
package org.study.test.china;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.util.List;

public class ChinaTest {
    private static SqlSessionFactory sqlSessionFactory;

    @BeforeAll
    public static void setUp() throws Exception {
        String resource = "org/study/test/mybatis-config.xml";
        try (Reader reader = Resources.getResourceAsReader(resource)) {
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        }
    }

    @Test
    public void testProvince01() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            final ProvinceMapper mapper = sqlSession.getMapper(ProvinceMapper.class);
            final Province province = mapper.selectByProvinceCode1("510000");
            System.out.println("查询结果：" + province);
        }
    }

    @Test
    public void testProvince02() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            final ProvinceMapper mapper = sqlSession.getMapper(ProvinceMapper.class);
            final ProvinceExtends provinceExtends = mapper.selectByProvinceCode2("510000");
            System.out.println("查询结果：" + provinceExtends);
        }
    }

    @Test
    public void testCity01() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            final CityMapper mapper = sqlSession.getMapper(CityMapper.class);
            final City city = mapper.selectByCityCode("510100");
            System.out.println("查询结果：" + city);
        }
    }

    @Test
    public void testCity02() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            final CityMapper mapper = sqlSession.getMapper(CityMapper.class);
            final List<City> cityList = mapper.selectByProvinceCode1("510000");
            System.out.println("查询结果：");
            cityList.forEach(System.out::println);
        }
    }

    @Test
    public void testCity03() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            final CityMapper mapper = sqlSession.getMapper(CityMapper.class);
            final List<CityExtends> cityExtendsList = mapper.selectByProvinceCode2("510000");
            System.out.println("查询结果：");
            cityExtendsList.forEach(System.out::println);
        }
    }

    @Test
    public void testArea01() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            final AreaMapper mapper = sqlSession.getMapper(AreaMapper.class);
            final Area area = mapper.selectByAreaCode("510107");
            System.out.println("查询结果：" + area);
        }
    }

    @Test
    public void testArea02() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            final AreaMapper mapper = sqlSession.getMapper(AreaMapper.class);
            final List<Area> areaList = mapper.selectByCityCode("510100");
            System.out.println("查询结果：");
            areaList.forEach(System.out::println);
        }
    }
}
