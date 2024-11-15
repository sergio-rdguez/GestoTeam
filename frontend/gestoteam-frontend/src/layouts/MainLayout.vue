<template>
    <div class="main-layout">
      <header class="app-header">
        <h1>GestoTeam</h1>
      </header>
      <main>
        <router-view />
      </main>
    </div>
  </template>
  
  <script>
  export default {
    name: "MainLayout",
  };
  </script>
  
  <style scoped>
  .main-layout {
    display: flex;
    flex-direction: column;
    height: 100vh;
  }
  
  .app-header {
    background-color: #007bff;
    color: white;
    padding: 10px;
    text-align: center;
    font-size: 1.5em;
  }
  
  main {
    flex: 1;
    padding: 10px;
    overflow-y: auto;
  }
  
  .bottom-nav {
    display: flex;
    justify-content: space-around;
    background-color: #f8f9fa;
    border-top: 1px solid #dee2e6;
    padding: 10px 0;
  }
  
  .nav-item {
    text-align: center;
    color: #007bff;
    text-decoration: none;
  }
  
  .nav-item:hover {
    color: #0056b3;
  }
  
  .nav-item i {
    font-size: 1.5em;
    margin-bottom: 5px;
    display: block;
  }
  </style>
  