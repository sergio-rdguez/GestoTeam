<template>
  <div class="tactical-board-page">
    <PageHeader title="Pizarra Táctica" :show-back-button="true" @back="goBack" />
    
    <div class="board-container">
      <TacticalBoard />
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router';
import PageHeader from '@/components/layout/PageHeader.vue';
import TacticalBoard from '@/components/exercises/TacticalBoard.vue';

const router = useRouter();

const goBack = () => {
  router.push('/my-resources');
};
</script>

<style scoped>
.tactical-board-page {
  height: calc(100vh - 120px);
  display: flex;
  flex-direction: column;
}

.board-container {
  flex: 1;
  margin-top: var(--spacing-4);
  border-radius: var(--border-radius-lg);
  overflow: hidden;
  box-shadow: var(--shadow-md);
}
</style>
