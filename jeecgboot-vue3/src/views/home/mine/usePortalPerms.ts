import { onMounted, ref } from 'vue';
import { usePermissionStore } from '/@/store/modules/permission';

/** 前台不进后台菜单，但简历、应聘这些按钮仍要按求职者权限显示。 */
export function usePortalPerms() {
  const ready = ref(false);
  const permissionStore = usePermissionStore();

  onMounted(async () => {
    try {
      const codes = permissionStore.getPermCodeList;
      if (!codes || codes.length === 0) {
        await permissionStore.changePermissionCode();
      }
    } catch (error) {
      console.warn('前台加载个人功能权限失败', error);
    } finally {
      ready.value = true;
    }
  });

  return ready;
}
