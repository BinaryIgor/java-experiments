# Outbox Pattern

* Problem
* Rozwiązanie - Outbox Pattern
* Konsekwencje/tradeoffy 
* Outbox pattern a rozproszona transakcja 
* Przykładowa implementacja
* Dyskusja/pytania

---


# Problem

> Chcemy zmienić stan serwisu/modułu, zapisując coś w jego bazie danych, i opublikować event/wysłać request do innego serwisu/serwisów/modułu/modułów.
> Event/request to może być wiadomość publikowana na system kolejkowy/message broker typu Kafka, Pub/Sub czy Rabbit, ale też event aplikacyjny w pamieci bądź zwykły request http do innego serwisu. 
> 
> Chcemy aby obie z tych operacji zakończyły się powodzeniem lub porażką; niedopuszczalne są sytuacje kiedy:
> * zmieniliśmy stan serwisu/modułu A, ale nie udało się opublikować eventu/wysłać requestu
> * nie udało się zmienić stanu serwisu/modułu A, ale udało się opublikować event/wysłać request

Wyrażając problem kodem, chcemy uzyskać taką gwarancje:
```
transaction {
  changeSomethingInDatabase();
  publishOrSendSomething();
}
```

---

## Konkretne przykłady

1. **Serwis tworzący konta użytkowników**  
Konto użytkownika tworzy się poprzez wywołanie odpowiedniego endpointu http w `user-service`.
Po stworzeniu takiego konta chcemy wysłać na topic Pub/Sub'owy (GCP) `user-created` event `UserCreated`. 
Na topic ten subskrybuje się `notification-service` i wysyła do usera odpowiedniego maila ze szczegółami aktywacji konta.
Jeżeli `notification-service` nie otrzyma eventu `UserCreated`, a uda się zapisać usera w bazie danych , w praktyce user nigdy nie otrzyma maila z linkiem aktywacyjnym i nigdy nie będzie mógł się zalogować.
Raczej nie chcemy żeby się tak stało ;)  
2. **Serwis z kampaniami reklamowymi (campaign-service)**  
Serwis ten, w wyniku rożnych zdarzeń w systemie (np. data końca kampanii bądź wyczerpany budżet dzienny/całkowity), powinen zmienić się jej stan z aktywnej (indeksowalnej) na nieaktywną (nieindeksowalną). 
Stan ten musimy zapisać w lokalnej bazie serwisu `campaign-service`, a następnie opublikować event `CampaignChanged` na topic Pub/Sub'owy - `campaign-changed`.
Zmiana stanu kampanii, ale brak publikacji eventu spowoduje wyświetlanie sie kampanii reklamowych, które nie powinny się wyświetlać oraz niewyświetlanie się tych, które powinny.
W takiej sytuacji tracimy pieniądze - raczej nie chcemy, żeby tak się stało.
3. **Niezależne moduły w modularnym monolicie**  
Przykład pierwszy z `user-service` i `notification-service` moglibyśmy zrealizowac na identycznej zasadzie w modularnym monolicie. 
Powiedzmy, że mamy moduły `user` i `notification`. W takim wypadku, event `UserCreated` zostałby opublikowany jako event aplikacyjny, w pamięci zamiast przez network za pomocą Pub/Sub'a i odebrany przez moduł `notifications`.
Po jego odebraniu moduł ten zrobiłby dokładnie to samo co serwis `notification-service` z przykładu pierwszego.

---

# Rozwiązanie - Outbox Pattern

Kod:
```
// dowolny obiekt/encja/aggregat
class CampaignState(val campaignId: UUID, val state: CampaignState) {
  fun toOutboxMessage() = 
    OutboxMessage(topic="campaign-state-changed",
      message=objectMapper.writeValueAsString(this),
      createdAt=clock.instant()
}

transaction {
  saveCampaignState(campaignState)
  saveOutboxMessage(campaignState.toOutboxMessage())
}

// Schedulowany oddzielnie proces
@Scheduled(fixedDelayString="PT5S")
fun execute() {
  val messagesToPublish = outboxMessages.getAll(limit=100)
  // Publikujemy na Pub/Sub GCP'owy,
  // ale tak samo może być to Kafka, endpoint HTTP, 
  // czy event aplikacyjny w pamięci modularnego monolitu
  val (publishedMessages, failedMessages) = publishOutboxMessagesToPubSub(messagesToPublish)
  outboxMessages.deleteBatch(publishedMessages)
}
```

Wyjaśnienie:
> Dokonujemy zmian w lokalnej bazie danych serwisu/modułu - może to być stworzenie rekordu/dokumentu, ale też jego modyfikacja bądź usunięcie.
> 
> Razem z tą zmianą zapisujemy event/request, który powinen zostać opublikowany/wysłany.
> 
> Jeżeli używamy transakcyjnej bazy danych:  
> event/request do wysłania może znajdować się w oddzielnej tabeli typu *outbox_message*,
> ponieważ tych dwóch operacji możemy dokonać w sposób atomowy (obie zakończą się powodzeniem lub żadna z nich).
> 
> Jeżeli używamy NoSQL'owej bazy danych, która nie wspiera/nie chcemy używać transakcji:  
> musimy uwzględnić dane o wiadomości/requeście do wysłania w schemacie naszych danych.
> Możemy np, dorzucić pole *events* do obiektu/dokumentu, który zapisujemy, zawierające liste eventów do wysłania i odpowiednio ją modyfikować.
> 
> Dodatkowo, musimy mieć niezależny, schedulowany proces, który z odpowiednią częstotliwością próbuje wysłać zapisane eventy/requesty zgodnie z ich specyfikacją (co i gdzie należy wysłać), a po udanej wysyłce - usuwa je.
> 
> Może się czasem zdarzyć, że eventy/requesty uda się wysłac, ale samo ich usunięcie z bazy zakończy się niepowodzeniem.
> W związku z tym, powinniśmy zapewnić idempotentność w obsłudze takich eventów/requestów po stronie ich odbiorcy.

---

## Konsekwencje/tradeoffy

1. Większe obciążenie bazy - musimy pisać do dodatkowej tabeli/pola oraz odpowiednio je potem akutalizować
2. Potrzebujemy oddzielnego procesu, który wysyła eventy/requesty i synchronizuje ten fakt z bazą danych. Proces ten potrzebuje zasobów, może się nie uruchomić, może zabraknąć mu pamięci, może obciążać CPU etc.
3. Decyzja architektoniczna - gdzie powinien żyć ten niezależny proces publikujący wiadomości? Razem z każdym serwisem/modułem? Co jeżeli mamy serwis w kilku instancjach? A może jako niezależny serwis? Jak upewnimy się, że wciąż działa?
4. Uproszczenie kodu i łatwość testowania - wystarczy, że sprawdzimy iż razem z modyfikacją naszych danych zapisaliśmy odpowiedni event/request do wysłania; niezależny proces jest odpowiedzialny za samą wysyłke.
5. Wysyłanie eventów/requestów, a następnie synchronizowanie tego stany z bazą danych to problem dość generyczny - łatwo napisać jakąś reużywalną bibliotekę/serwis, który zrobi to za nas.
6. Spójność - przy prawidłowej implementacji mamy 100% gwarancji, że dane pomiędzy serwisami/modułami będą spójne. Zmiana stanu + zapis eventu/requestu może odbywać się w sposób atomowy/transakcyjny; event/request zawsze (eventually) dotrze do odbiorcy

## Outbox Pattern a rozproszona transakcja

Co warte zaznaczenia, pattern ten nie rozwiązuje problemu rozproszonej transakcji!

W przypadku rozproszonej transakcji, powiedzmy że mamy następujący flow:
* serwis A
* serwis B
* serwis C

W konsekwencji:
* Jeżeli akcja *serwisu B* zakończy się niepowodzeniem, musimy cofnąć akcje *serwisu A*.
* Jeżeli akcja *serwisu C* zakończy się niepowodzeniem, musimy cofnąć akcje *serwisu B* i *serwisu A*.

*Outbox Pattern* w żaden sposób nie rozwiązuje tego problemu.
Daje on tylko gwarancje, że zawsze jesteśmy w stanie zmodyfikować stan bazy danych i wysłać event/request w sposób atomowy.

W konsekwencji, *Outbox Pattern* jest elementem rozwiązań problemu rozproszonej transakcji;
*Saga* jest jednym z rozwiązań probemu rozproszonej transakcji; *Outbox Pattern* jest nierozłącznym elementem tego rozwiązania.


## Przykładowa implementacja w adsach

Opis/Libka


## Dyskusja/Pytania