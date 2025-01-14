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

import algolia.AlgoliaDsl._
import algolia.AlgoliaTest
import algolia.responses.{TaskIndexing, TasksMultipleIndex}

import scala.concurrent.Future

class TaskStatusIntegrationTest extends AlgoliaTest {

  val indexToGetTaskStatus: String = getTestIndexName("indexToGetTaskStatus")

  after {
    clearIndices(indexToGetTaskStatus)
  }

  describe("task") {
    it("should get the status of a single object task") {
      val create: Future[TaskIndexing] = AlgoliaTest.client.execute {
        index into indexToGetTaskStatus `object` ObjectToGet("1", "toto")
      }

      val task = taskShouldBeCreated(create)

      eventually {
        val status = AlgoliaTest.client.execute { getStatus task task from indexToGetTaskStatus }
        whenReady(status) { result =>
          result.status shouldBe "published"
        }
      }
    }

    it("should get the status of a multiple objects task") {
      val create: Future[TasksMultipleIndex] = AlgoliaTest.client.execute {
        batch(
          index into indexToGetTaskStatus `object` ObjectToGet("1", "toto"),
          index into indexToGetTaskStatus `object` ObjectToGet("2", "tata")
        )
      }

      val task = taskShouldBeCreated(create)

      eventually {
        val status = AlgoliaTest.client.execute { getStatus task task from indexToGetTaskStatus }
        whenReady(status) { result =>
          result.status shouldBe "published"
        }
      }
    }
  }

}
