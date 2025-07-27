<template>
  <router-view />
  <NotificationContainer />
</template>

<script setup>
import NotificationContainer from '@/components/common/NotificationContainer.vue';
</script>

<style>
body {
  font-family: 'Roboto', Arial, sans-serif;
  margin: 0;
  background-color: #f4f4f9;
  color: #333;
}

#app {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}
</style>
