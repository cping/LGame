/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.5
 */
package loon;

/**
 * 使用java 1.7以后开始提供的AutoCloseable接口重载关闭项 (0.5版前为dispose，为统一Java标准替换为close，另外，使用此接口后，若资源不关闭，在eclipse或netbeans等ide中会有提示)
 */
public interface LRelease extends AutoCloseable {

  void close ();
  
}
