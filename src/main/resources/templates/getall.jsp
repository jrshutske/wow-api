<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" th:replace="~{fragments/layout :: layout (~{::body},'db')}">
<body>
<div class="container">
  <h1>getall output</h1>
  <ul th:each="record : ${records}">
    <li th:text="${record}"/>
  </ul>
</div>
</body>
</html>