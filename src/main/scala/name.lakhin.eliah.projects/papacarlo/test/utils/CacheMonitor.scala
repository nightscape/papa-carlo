/*
   Copyright 2013 Ilya Lakhin (Илья Александрович Лахин)

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package name.lakhin.eliah.projects
package papacarlo.test.utils

import name.lakhin.eliah.projects.papacarlo.{Lexer, Syntax}
import name.lakhin.eliah.projects.papacarlo.syntax.Cache

final class CacheMonitor(lexer: Lexer, syntax: Syntax)
  extends SyntaxMonitor(lexer, syntax) {

  private var cacheLog = List.empty[(Symbol, String)]

  private val onCacheCreate = (cache: Cache) =>
    cacheLog ::= ('create, cacheInfo(cache))

  private val onCacheInvalidate = (cache: Cache) =>
    cacheLog ::= ('invalidate, cacheInfo(cache))

  private val onCacheRemove = (cache: Cache) =>
    cacheLog ::= ('remove, cacheInfo(cache))

  private def cacheInfo(cache: Cache) =
    "Node " + cache.node.id + ". Fragment:" +
      (
        if (this.shortOutput) " " + cache.fragment + "."
        else "\n" + cache.fragment.highlight(Some(10))
      )

  def getResult = unionLog(cacheLog)

  def prepare() {
    cacheLog = Nil
    syntax.onCacheCreate.bind(onCacheCreate)
    syntax.onCacheInvalidate.bind(onCacheInvalidate)
    syntax.onCacheRemove.bind(onCacheRemove)
  }

  def release() {
    cacheLog = Nil
    syntax.onCacheCreate.unbind(onCacheCreate)
    syntax.onCacheInvalidate.unbind(onCacheInvalidate)
    syntax.onCacheRemove.unbind(onCacheRemove)
  }
}