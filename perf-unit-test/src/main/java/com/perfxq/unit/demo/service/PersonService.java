/*
 * Copyright 2002-2016 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.perfxq.unit.demo.service;
import java.util.List;

import com.perfxq.unit.demo.dao.PersonDao;
import com.perfxq.unit.demo.entity.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class PersonService {

	@Autowired
	private PersonDao personDao;

	@SuppressWarnings("unchecked")
	public List<Person> find(String name) {
		List<Person> list = personDao.find(name);
		return list;
	}

	public List<Person> selectAll(){
		List<Person> list = personDao.selectAll();
		return list;
	}

	public List<Person> queryByTitle(String title){
		List<Person> list = personDao.queryByTitle(title);
		return list;
	}
}
