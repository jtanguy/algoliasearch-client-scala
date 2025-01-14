/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Algolia
 * http://www.algolia.com/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package algolia.integration

import algolia.responses.{LogType, Logs}
import algolia.AlgoliaTest
import algolia.AlgoliaDsl._

import scala.concurrent.Future

class LogIntegrationTest extends AlgoliaTest {

  val indexToSearch: String = getTestIndexName("indexToSearch")

  after {
    clearIndices(
      indexToSearch
    )
  }

  before {
    val obj = Test("algolia", 10, alien = false)
    val insert1 = AlgoliaTest.client.execute {
      index into indexToSearch objectId "563481290" `object` obj
    }

    taskShouldBeCreatedAndWaitForIt(insert1, indexToSearch)
  }

  it("should get the logs") {
    val result: Future[Logs] = AlgoliaTest.client.execute {
      getLogs offset 0 length 10 `type` LogType.all
    }

    whenReady(result) { r =>
      r.logs shouldNot be(empty)
    }
  }

}
