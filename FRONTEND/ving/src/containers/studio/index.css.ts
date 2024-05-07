import { betweenBox, columnbox, defaultBox, endBox, rowbox } from '@/styles/box.css'
import { plainButton } from '@/styles/common.css'
import { bold } from '@/styles/fonts.css'
import { vars } from '@/styles/vars.css'
import { style } from '@vanilla-extract/css'

// common.tsx
export const container = style({
  margin: `${vars.space['2x']} ${vars.space['8x']}`,
})

export const title = style([
  bold,
  {
    fontSize: vars.fontSize['2x'],
    margin: `0 0 ${vars.space['2x']} 0`
  }
])

export const subtitle = style([
  bold,
  {
    fontSize: vars.fontSize['1.5x'],
  }
])

export const itemTitle = style([
  bold,
  {
    fontSize: vars.fontSize['1x'],
  }
])

export const contentBox = style({
  flex: 1,
  display: 'grid',
  gridGap: vars.space['2x'],
})

export const card = style({
  background: vars.colors.white,
  boxShadow: vars.boxShadow['2x'],
  transition: '0.3s',
  borderRadius: vars.borderRadius['1x'],
  margin: `0 0 ${vars.space['4x']} 0`
})


// StudioMenu.tsx

export const sideMenuContainer = style({
  position: 'sticky',
  minWidth: 150,
  background: vars.colors.lightGray,
  top: 0,
  bottom: 0,
  height: '100vh',
  padding: `${vars.space['8x']} ${vars.space['1x']} ${vars.space['1x']} ${vars.space['1x']}`,
})

export const menuItem = style([
  plainButton,
  {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'flex-start',
    padding: `0 0 0 ${vars.space['1.5x']}`,
    height: '40px',
    ':hover': {
      backgroundColor: vars.colors.gray,
      color: vars.colors.white,
      borderRadius: vars.borderRadius['0.5x']
    },
  }
])

// Dashboard.tsx

export const dashboardContainer = style([
  container,
  {
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'flex-start',
    flexDirection: 'row'
  }
])

export const dashboardItemContent = style({
  display: 'grid',
  gridGap: vars.space['2x'],
  margin: `0 0 ${vars.space['2x']} 0`,
})

export const leftBox = style([
  columnbox,
  {
    flex: 1,
    marginRight: vars.space['2x']
  }
])

export const rightBox = style([
  columnbox,
  {
    flex: 1,
    marginLeft: vars.space['2x']
  }
])

export const dashboardBox = style([
  card,
  {
    width: '100%',
    padding: vars.space['4x']
  }
])

export const dashboardOrder = style({
  display: 'block',
  fontSize: vars.fontSize['0.5x'],
  color: vars.colors.darkGray,
})

export const dashboardOrderCf = style({
  display: 'block',
  fontSize: vars.fontSize['0.5x'],
  color: vars.colors.darkGray,
})


// StartStreaming.tsx

export const contentContainer = style({
  display: 'flex',
  width: '100%',
  flexDirection: 'row',
});

export const startStreamingRightBox = style([
  columnbox,
  {
    height: '100vh',
    width: 500,
    position: 'sticky',
    top: 0,
    bottom: 0,
    padding: `${vars.space['8x']} 0 0 0`,
    margin: vars.space.none,
  }
])

export const studioStreamingContainer = style({
  flexGrow: 1,
})

// StudioStreaming.tsx
export const streamingInfoContainer = style({
  width: '100%',
  padding: vars.space['2x']
})

export const streamingInfoItem = style([
  rowbox,
  {
    padding: `${vars.space['1x']} 0`
  }
])

export const streamingInfoTitle = style([
  bold,
  defaultBox,
  {
    flex: '0 0 30%', // flex-grow, flex-shrink, flex-basis
  }
])

export const streamingInfoContent = style([
  defaultBox,
  {
    flex: '0 0 70%',
  }
]);


export const updateButtonBox = style([
  endBox,
  {
    padding: `${vars.space['1x']} 0`
  }
])

// Static.tsx

export const SummuryContainer = style([
  card,
  betweenBox,
  {
    padding: `${vars.space['2x']} ${vars.space['8x']}`
  }
])

export const SummuryItemBox = style([
  columnbox,
])



