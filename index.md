# 복원력 있는 애플리케이션 구현

마이크로 서비스와 클라우드 기반 애플리케이션에서는 결국 발생하게 되는 부분 오류를 수용해야 합니다. 부분 오류에 탄력적으로 대처할 수 있도록 애플리케이션을 디자인해야 합니다.

복원력은 오류를 복구하고 계속 작업을 진행하는 기능입니다. 이는 오류를 방지하는 기능이 아닌 오류가 발생할 수 있다는 사실을 받아들이고 가동 중지 시간 또는 데이터 손실을 방지할 수 있도록 해당 오류에 응답하는 기능입니다. 복원력의 목표는 오류 발생 후 애플리케이션을 완전히 작동 중인 상태로 되돌리는 것입니다.

마이크로 서비스 기반 애플리케이션을 디자인하고 배포하기는 쉽지 않습니다. 그러나 일부 오류가 명확히 발생하는 환경에서 애플리케이션을 계속 실행하는 작업도 필요합니다. 따라서 애플리케이션에 복원력이 있어야 합니다. 응용 프로그램은 클라우드에서의 네트워크 중단 또는 노드 또는 VM 충돌과 같은 부분 오류에 대처할 수 있도록 디자인되어야 합니다. 클러스터 내에서 다른 노드로 이동한 마이크로 서비스(컨테이너)도 애플리케이션에서 일시적 단기 오류를 유발할 수 있습니다.

애플리케이션의 여러 개별 구성 요소도 상태 모니터링 기능과 통합해야 합니다. 이 장의 지침을 따라 가동 중지 시간 또는 복잡한 클라우드 기반 배포의 일반적인 문제가 발생하는 환경에서도 원활하게 작동하는 애플리케이션을 만들 수 있습니다.

[복원력 있는 애플리케이션 구현](index.md)
[부분 실패 처리](handle-partial-failure.md)
[부분 실패 처리 전략](partial-failure-strategies.md)
[지수 백오프를 사용하여 다시 시도 구현](implement-retries-exponential-backoff.md)
[복원력 있는 Entity Framework Core SQL 연결 구현](implement-resilient-entity-framework-core-sql-connections.md)
[IHttpClientFactory를 사용하여 복원력 있는 HTTP 요청 구현](use-httpclientfactory-to-implement-resilient-http-requests.md)
[Polly를 통해 지수 백오프를 사용하여 HTTP 호출 다시 시도 구현](implement-http-call-retries-exponential-backoff-polly.md)
[회로 차단기 패턴 구현](implement-circuit-breaker-pattern.md)
[상태 모니터링](monitor-app-health.md)