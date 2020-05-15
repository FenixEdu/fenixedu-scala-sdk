package org.fenixedu.sdk

import java.util.Locale
import org.http4s.Uri

class AtRootSpec extends Utils {
  "FenixEduClient" should {
    "retrieve about" in {
      client.about.idempotently { about =>
        about.language shouldBe new Locale("pt", "PT")
        about.institutionUrl shouldBe Uri.unsafeFromString("https://tecnico.ulisboa.pt/")
      }
    }

    "retrieve academic terms" in {
      client.academicTerms.idempotently { terms =>
        terms.keys should contain ("2020/2021")
        terms("2020/2021") should have length 2
      }
    }

    "retrieve contacts" in {
      client.contacts.idempotently { contacts =>
        contacts.map(_.name) should contain ("Campus Alameda")
      }
    }

    "retrieve parking" in {
      client.parking.idempotently { parking =>
        val alameda = parking("Alameda")
        alameda.campus shouldBe "Alameda"
        alameda.freeSlots should be <= alameda.total
      }
    }

    "retrieve shuttle" in {
      client.shuttle.idempotently { shuttle =>
        shuttle.stations.map(_.name) should contain ("Alameda")
        shuttle.date should not be empty
        shuttle.trips should not be empty
      }
    }
  }
}
