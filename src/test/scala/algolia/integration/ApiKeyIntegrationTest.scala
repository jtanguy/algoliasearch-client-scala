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
import algolia.objects.{Acl, ApiKey}

import scala.language.postfixOps

class ApiKeyIntegrationTest extends AlgoliaTest {

  var keyName = ""

  describe("global keys") {

    it("should add/get/list/update/delete/restore a key") {
      val addKey = AlgoliaTest.client.execute {

        add key ApiKey(acl = Some(Seq(Acl.addObject)))
      }

      whenReady(addKey) { r =>
        r.key shouldNot be(empty)
        keyName = r.key
      }

      Thread.sleep(5000) //ok let's wait propagation

      val getKey = AlgoliaTest.client.execute {
        get key keyName
      }

      whenReady(getKey) { r =>
        r.acl should equal(Some(Seq(Acl.addObject)))
      }

      val listKeys = AlgoliaTest.client.execute {
        list keys
      }

      whenReady(listKeys) { r =>
        r.keys shouldNot be(empty)
      }

      val updateKey = AlgoliaTest.client.execute {

        update key keyName `with` ApiKey(validity = Some(10))
      }

      whenReady(updateKey) { r =>
        r.key should be(keyName)
      }

      val deleteKey = AlgoliaTest.client.execute {
        delete key keyName
      }

      whenReady(deleteKey) { r =>
        r.deletedAt shouldNot be(empty)
      }

      println(keyName)

      var isKeyDeleted = false

      while (!isKeyDeleted) {
        val getKey = AlgoliaTest.client.execute {
          get key keyName
        }

        getKey.onComplete { t =>
          isKeyDeleted = t.isFailure
        }

        Thread.sleep(800)
      }

      val restoreKey = AlgoliaTest.client.execute {
        restore key keyName
      }

      whenReady(restoreKey) { r =>
        var isKeyRestored = false

        while (!isKeyRestored) {

          val getRestoredKey = AlgoliaTest.client.execute {
            get key keyName
          }

          getRestoredKey.onComplete { t =>
            isKeyRestored = t.isFailure
          }

          Thread.sleep(800)
        }
      }

      val deleteKeyAfterRestore = AlgoliaTest.client.execute {
        delete key keyName
      }

      whenReady(deleteKeyAfterRestore) { r =>
        r.deletedAt shouldNot be(empty)
      }
    }
  }
}
